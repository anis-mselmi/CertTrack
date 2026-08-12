package com.certtrack.certification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import org.hibernate.validator.constraints.URL;

public record CertificationRequest(
        @NotBlank @Size(max = 160) String title,
        @NotBlank @Size(max = 160) String issuer,
        @NotNull @PastOrPresent LocalDate issueDate,
        @NotNull LocalDate expiryDate,
        @URL @Size(max = 500) String credentialUrl,
        Set<@NotBlank @Size(max = 60) String> skillTags
) {
}
