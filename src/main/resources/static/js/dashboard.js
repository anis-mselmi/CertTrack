let jwtToken = localStorage.getItem("certtrackToken");
let skillChart;

const loginForm = document.getElementById("login-form");
const loginStatus = document.getElementById("login-status");
const tableBody = document.getElementById("certification-table");

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            username: document.getElementById("username").value,
            password: document.getElementById("password").value
        })
    });

    if (!response.ok) {
        loginStatus.textContent = "Login failed";
        return;
    }

    const data = await response.json();
    jwtToken = data.token;
    localStorage.setItem("certtrackToken", jwtToken);
    loginStatus.textContent = `Authenticated for ${data.expiresInMinutes} minutes`;
    await loadCertifications();
});

async function loadCertifications() {
    if (!jwtToken) {
        loginStatus.textContent = "Login required to load certifications";
        return;
    }

    const response = await fetch("/api/certifications", {
        headers: {Authorization: `Bearer ${jwtToken}`}
    });

    if (!response.ok) {
        loginStatus.textContent = "Token invalid or expired";
        return;
    }

    const certifications = await response.json();
    renderTable(certifications);
    renderSkillChart(certifications);
}

function renderTable(certifications) {
    tableBody.innerHTML = "";
    if (certifications.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="5" class="muted">No certifications yet. Add records via the REST API or Swagger UI.</td></tr>`;
        return;
    }

    for (const certification of certifications) {
        const row = document.createElement("tr");
        const countdown = certification.daysUntilExpiry < 0
            ? `${Math.abs(certification.daysUntilExpiry)} days ago`
            : `${certification.daysUntilExpiry} days left`;
        row.innerHTML = `
            <td>${escapeHtml(certification.title)}</td>
            <td>${escapeHtml(certification.issuer)}</td>
            <td>${certification.expiryDate}<br><span class="muted">${countdown}</span></td>
            <td><span class="badge ${certification.status}">${certification.status.replace("_", " ")}</span></td>
            <td>${certification.skillTags.map(escapeHtml).join(", ")}</td>
        `;
        tableBody.appendChild(row);
    }
}

function renderSkillChart(certifications) {
    const counts = {};
    for (const certification of certifications) {
        for (const skillTag of certification.skillTags) {
            counts[skillTag] = (counts[skillTag] || 0) + 1;
        }
    }

    const labels = Object.keys(counts);
    const values = Object.values(counts);
    if (skillChart) {
        skillChart.destroy();
    }

    skillChart = new Chart(document.getElementById("skill-chart"), {
        type: "bar",
        data: {
            labels,
            datasets: [{
                label: "Certifications",
                data: values,
                backgroundColor: "#2563eb"
            }]
        },
        options: {
            responsive: true,
            plugins: {legend: {display: false}},
            scales: {y: {beginAtZero: true, ticks: {precision: 0}}}
        }
    });
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

loadCertifications();
