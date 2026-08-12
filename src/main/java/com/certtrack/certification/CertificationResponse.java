package com.certtrack.certification;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;

public record CertificationResponse(
        Long id,
        String title,
        String issuer,
        LocalDate issueDate,
        LocalDate expiryDate,
        String credentialUrl,
        Set<String> skillTags,
        CertificationStatus status,
        long daysUntilExpiry
) {
    static CertificationResponse from(Certification certification) {
        return new CertificationResponse(
                certification.getId(),
                certification.getTitle(),
                certification.getIssuer(),
                certification.getIssueDate(),
                certification.getExpiryDate(),
                certification.getCredentialUrl(),
                certification.getSkillTags(),
                certification.getStatus(),
                ChronoUnit.DAYS.between(LocalDate.now(), certification.getExpiryDate())
        );
    }
}
