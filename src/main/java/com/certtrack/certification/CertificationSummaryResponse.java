package com.certtrack.certification;

import java.time.LocalDate;
import java.util.List;

/** A compact, server-calculated snapshot for dashboard cards and alerts. */
public record CertificationSummaryResponse(
        long total,
        long active,
        long expiringSoon,
        long expired,
        long skillCount,
        LocalDate nextExpiryDate,
        String nextExpiryTitle,
        List<SkillCoverage> skillCoverage
) {
    public record SkillCoverage(String skill, long certifications) {
    }
}
