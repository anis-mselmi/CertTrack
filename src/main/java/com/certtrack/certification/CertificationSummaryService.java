package com.certtrack.certification;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificationSummaryService {

    private final CertificationRepository repository;

    public CertificationSummaryService(CertificationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public CertificationSummaryResponse summarize() {
        List<Certification> certifications = repository.findAll();
        Map<String, Long> skillCounts = new LinkedHashMap<>();
        for (Certification certification : certifications) {
            for (String skill : certification.getSkillTags()) {
                skillCounts.merge(skill, 1L, Long::sum);
            }
        }

        certifications.sort(Comparator.comparing(Certification::getExpiryDate));
        Certification nextExpiry = certifications.stream()
                .filter(certification -> !certification.getExpiryDate().isBefore(LocalDate.now()))
                .findFirst()
                .orElse(null);

        return new CertificationSummaryResponse(
                certifications.size(),
                countByStatus(certifications, CertificationStatus.ACTIVE),
                countByStatus(certifications, CertificationStatus.EXPIRING_SOON),
                countByStatus(certifications, CertificationStatus.EXPIRED),
                skillCounts.size(),
                nextExpiry == null ? null : nextExpiry.getExpiryDate(),
                nextExpiry == null ? null : nextExpiry.getTitle(),
                skillCounts.entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .map(entry -> new CertificationSummaryResponse.SkillCoverage(entry.getKey(), entry.getValue()))
                        .toList()
        );
    }

    private long countByStatus(List<Certification> certifications, CertificationStatus status) {
        return certifications.stream().filter(certification -> certification.getStatus() == status).count();
    }
}
