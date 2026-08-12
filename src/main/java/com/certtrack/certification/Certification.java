package com.certtrack.certification;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String issuer;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    private String credentialUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "certification_skill_tags", joinColumns = @JoinColumn(name = "certification_id"))
    @Column(name = "skill_tag", nullable = false)
    private Set<String> skillTags = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCredentialUrl() {
        return credentialUrl;
    }

    public void setCredentialUrl(String credentialUrl) {
        this.credentialUrl = credentialUrl;
    }

    public Set<String> getSkillTags() {
        return skillTags;
    }

    public void setSkillTags(Set<String> skillTags) {
        this.skillTags = skillTags == null ? new LinkedHashSet<>() : skillTags;
    }

    public CertificationStatus getStatus() {
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) {
            return CertificationStatus.EXPIRED;
        }
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);
        if (daysUntilExpiry <= 30) {
            return CertificationStatus.EXPIRING_SOON;
        }
        return CertificationStatus.ACTIVE;
    }
}
