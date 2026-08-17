let jwtToken = localStorage.getItem("certtrackToken");
let certifications = [];
let skillChart;

const $ = (id) => document.getElementById(id);
const loginForm = $("login-form");
const loginStatus = $("login-status");
const tableBody = $("certification-table");

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    loginStatus.textContent = "Checking access…";
    try {
        const response = await fetch("/api/auth/login", {
            method: "POST", headers: {"Content-Type": "application/json"},
            body: JSON.stringify({username: $("username").value, password: $("password").value})
        });
        if (!response.ok) throw new Error("Invalid username or password.");
        const data = await response.json();
        jwtToken = data.token;
        localStorage.setItem("certtrackToken", jwtToken);
        loginStatus.textContent = `Access active for ${data.expiresInMinutes} minutes.`;
        await loadCertifications();
    } catch (error) { loginStatus.textContent = error.message || "Connection failed. Please try again."; }
});

$("logout-button").addEventListener("click", () => {
    jwtToken = null; localStorage.removeItem("certtrackToken");
    certifications = []; renderDashboard(); loginStatus.textContent = "Signed out. Sign in to view your workspace.";
});

$("search-input").addEventListener("input", renderTableFromFilters);
$("status-filter").addEventListener("change", renderTableFromFilters);
$("add-cert-button").addEventListener("click", () => { $("add-panel").hidden = false; $("cert-title").focus(); $("add-panel").scrollIntoView({behavior: "smooth", block: "center"}); });
$("close-add-button").addEventListener("click", () => { $("add-panel").hidden = true; });
$("certification-form").addEventListener("submit", createCertification);

async function loadCertifications() {
    if (!jwtToken) { tableBody.innerHTML = `<tr><td colspan="5" class="empty-cell">Sign in to load your certifications.</td></tr>`; return; }
    tableBody.innerHTML = `<tr><td colspan="5" class="empty-cell">Loading your records…</td></tr>`;
    try {
        const headers = {Authorization: `Bearer ${jwtToken}`};
        const [response, summaryResponse] = await Promise.all([
            fetch("/api/certifications", {headers}),
            fetch("/api/certifications/summary", {headers})
        ]);
        if (response.status === 401 || response.status === 403) throw new Error("Your session expired. Please sign in again.");
        if (!response.ok) throw new Error("Could not load certifications.");
        certifications = await response.json();
        if (summaryResponse.ok) renderSummary(await summaryResponse.json());
        renderDashboard();
    } catch (error) { tableBody.innerHTML = `<tr><td colspan="5" class="empty-cell">${escapeHtml(error.message)}</td></tr>`; $("table-status").textContent = ""; }
}

function renderSummary(summary) {
    $("total-count").textContent = summary.total;
    $("active-count").textContent = summary.active;
    $("expiring-count").textContent = summary.expiringSoon;
    $("expired-count").textContent = summary.expired;
}

function renderDashboard() {
    const counts = certifications.reduce((result, cert) => { result[cert.status] = (result[cert.status] || 0) + 1; return result; }, {});
    $("total-count").textContent = certifications.length;
    $("active-count").textContent = counts.ACTIVE || 0;
    $("expiring-count").textContent = counts.EXPIRING_SOON || 0;
    $("expired-count").textContent = counts.EXPIRED || 0;
    renderTableFromFilters(); renderSkillChart();
}

function renderTableFromFilters() {
    const query = $("search-input").value.trim().toLowerCase();
    const status = $("status-filter").value;
    const filtered = certifications.filter((cert) => {
        const haystack = [cert.title, cert.issuer, ...(cert.skillTags || [])].join(" ").toLowerCase();
        return (!query || haystack.includes(query)) && (!status || cert.status === status);
    });
    renderTable(filtered);
    $("table-status").textContent = certifications.length && filtered.length !== certifications.length ? `${filtered.length} of ${certifications.length} records shown` : "";
}

function renderTable(records) {
    if (!records.length) { tableBody.innerHTML = `<tr><td colspan="5" class="empty-cell">${certifications.length ? "No certifications match these filters." : "No certifications yet. Add your first record above."}</td></tr>`; return; }
    tableBody.innerHTML = records.map((certification) => {
        const countdown = certification.daysUntilExpiry < 0 ? `${Math.abs(certification.daysUntilExpiry)} days ago` : `${certification.daysUntilExpiry} days left`;
        const skills = (certification.skillTags || []).map((skill) => `<span class="skill-tag">${escapeHtml(skill)}</span>`).join("");
        return `<tr><td><div class="credential-title">${escapeHtml(certification.title)}</div><span class="muted">Issued ${escapeHtml(certification.issueDate)}</span></td><td>${escapeHtml(certification.issuer)}</td><td>${escapeHtml(certification.expiryDate)}<br><span class="countdown">${countdown}</span></td><td><span class="badge ${escapeHtml(certification.status)}">${escapeHtml(certification.status.replace("_", " "))}</span></td><td>${skills}</td></tr>`;
    }).join("");
}

function renderSkillChart() {
    const counts = {};
    certifications.forEach((cert) => (cert.skillTags || []).forEach((skill) => counts[skill] = (counts[skill] || 0) + 1));
    if (skillChart) skillChart.destroy();
    $("chart-empty").hidden = !Object.keys(counts).length;
    if (!Object.keys(counts).length) return;
    skillChart = new Chart($("skill-chart"), {type: "bar", data: {labels: Object.keys(counts), datasets: [{data: Object.values(counts), backgroundColor: "#27694d", borderRadius: 4, barThickness: 16}]}, options: {indexAxis: "y", responsive: true, maintainAspectRatio: false, plugins: {legend: {display: false}}, scales: {x: {beginAtZero: true, ticks: {precision: 0}, grid: {color: "#edf0ec"}}, y: {grid: {display: false}, ticks: {color: "#71807b", font: {family: "Manrope"}}}}}});
}

async function createCertification(event) {
    event.preventDefault();
    if (!jwtToken) { $("cert-form-status").textContent = "Sign in before adding a record."; return; }
    const status = $("cert-form-status"); status.textContent = "Saving…";
    const payload = {title: $("cert-title").value.trim(), issuer: $("cert-issuer").value.trim(), issueDate: $("cert-issue-date").value, expiryDate: $("cert-expiry-date").value, credentialUrl: $("cert-url").value.trim() || null, skillTags: $("cert-skills").value.split(",").map((tag) => tag.trim()).filter(Boolean)};
    try {
        const response = await fetch("/api/certifications", {method: "POST", headers: {"Content-Type": "application/json", Authorization: `Bearer ${jwtToken}`}, body: JSON.stringify(payload)});
        if (!response.ok) { const body = await response.json().catch(() => ({})); throw new Error(body.message || "Could not save certification."); }
        $("certification-form").reset(); $("add-panel").hidden = true; status.textContent = ""; await loadCertifications();
    } catch (error) { status.textContent = error.message; }
}

function escapeHtml(value) { return String(value ?? "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll('"', "&quot;").replaceAll("'", "&#039;"); }

if (jwtToken) { loginStatus.textContent = "Restoring your secure session…"; loadCertifications(); }
