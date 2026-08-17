package com.certtrack.certification;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certifications")
public class CertificationController {

    private final CertificationService service;
    private final CertificationSummaryService summaryService;

    public CertificationController(CertificationService service, CertificationSummaryService summaryService) {
        this.service = service;
        this.summaryService = summaryService;
    }

    @GetMapping("/summary")
    public CertificationSummaryResponse summary() {
        return summaryService.summarize();
    }

    @GetMapping
    public List<CertificationResponse> findAll(
            @RequestParam(required = false) CertificationStatus status,
            @RequestParam(required = false) String skillTag
    ) {
        return service.findAll(status, skillTag);
    }

    @GetMapping("/{id}")
    public CertificationResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<CertificationResponse> create(@Valid @RequestBody CertificationRequest request) {
        CertificationResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/certifications/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public CertificationResponse update(@PathVariable Long id, @Valid @RequestBody CertificationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
