package com.certtrack.certification;

import com.certtrack.common.BadRequestException;
import com.certtrack.common.ResourceNotFoundException;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CertificationService {

    private final CertificationRepository repository;

    public CertificationService(CertificationRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CertificationResponse> findAll(CertificationStatus status, String skillTag) {
        return repository.findAll().stream()
                .filter(certification -> status == null || certification.getStatus() == status)
                .filter(certification -> skillTag == null || hasSkillTag(certification, skillTag))
                .sorted(Comparator.comparing(Certification::getExpiryDate))
                .map(CertificationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CertificationResponse findById(Long id) {
        return CertificationResponse.from(getCertification(id));
    }

    @Transactional
    public CertificationResponse create(CertificationRequest request) {
        Certification certification = new Certification();
        applyRequest(certification, request);
        return CertificationResponse.from(repository.save(certification));
    }

    @Transactional
    public CertificationResponse update(Long id, CertificationRequest request) {
        Certification certification = getCertification(id);
        applyRequest(certification, request);
        return CertificationResponse.from(repository.save(certification));
    }

    @Transactional
    public void delete(Long id) {
        Certification certification = getCertification(id);
        repository.delete(certification);
    }

    private Certification getCertification(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found: " + id));
    }

    private void applyRequest(Certification certification, CertificationRequest request) {
        if (request.issueDate().isAfter(request.expiryDate())) {
            throw new BadRequestException("issueDate must be on or before expiryDate");
        }
        certification.setTitle(request.title().trim());
        certification.setIssuer(request.issuer().trim());
        certification.setIssueDate(request.issueDate());
        certification.setExpiryDate(request.expiryDate());
        certification.setCredentialUrl(normalizeNullable(request.credentialUrl()));
        certification.setSkillTags(normalizeTags(request.skillTags()));
    }

    private Set<String> normalizeTags(Set<String> skillTags) {
        if (skillTags == null) {
            return new LinkedHashSet<>();
        }
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        for (String skillTag : skillTags) {
            if (skillTag != null && !skillTag.isBlank()) {
                normalized.add(skillTag.trim());
            }
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean hasSkillTag(Certification certification, String skillTag) {
        String normalized = skillTag.trim().toLowerCase(Locale.ROOT);
        return certification.getSkillTags().stream()
                .map(tag -> tag.toLowerCase(Locale.ROOT))
                .anyMatch(tag -> tag.equals(normalized));
    }
}
