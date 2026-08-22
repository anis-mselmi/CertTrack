# Graph Report - Java-web-app  (2026-08-22)

## Corpus Check
- Corpus is ~4,264 words - fits in a single context window. You may not need a graph.

## Summary
- 194 nodes · 413 edges · 13 communities (10 shown, 3 thin omitted)
- Extraction: 92% EXTRACTED · 8% INFERRED · 0% AMBIGUOUS · INFERRED: 33 edges (avg confidence: 0.79)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Certification API & Persistence
- Authentication & JWT
- Certification Domain Entity
- Error Handling
- Security Configuration
- Frontend Dashboard (SPA)
- JSON Auth Entry Point
- Maven Wrapper Script
- Summary & Web Routing
- Integration Tests
- Application Bootstrap
- Project / IDE Config
- Build Coordinates

## God Nodes (most connected - your core abstractions)
1. `Certification` - 24 edges
2. `CertificationService` - 16 edges
3. `JwtService` - 14 edges
4. `CertificationController` - 13 edges
5. `CertificationResponse` - 11 edges
6. `ErrorResponse` - 10 edges
7. `GlobalExceptionHandler` - 10 edges
8. `SecurityConfig` - 10 edges
9. `AuthController` - 9 edges
10. `JwtAuthenticationFilter` - 9 edges

## Surprising Connections (you probably didn't know these)
- `Dashboard UI (SPA)` --CALLS_API--> `AuthController`  [INFERRED]
  src/main/resources/templates/dashboard.html → src/main/java/com/certtrack/auth/AuthController.java
- `Dashboard UI (SPA)` --CALLS_API--> `CertificationController`  [INFERRED]
  src/main/resources/templates/dashboard.html → src/main/java/com/certtrack/certification/CertificationController.java
- `Dashboard UI (SPA)` --INVOKES--> `loadCertifications()`  [EXTRACTED]
  src/main/resources/templates/dashboard.html → src/main/resources/static/js/dashboard.js
- `Dashboard UI (SPA)` --INVOKES--> `createCertification()`  [EXTRACTED]
  src/main/resources/templates/dashboard.html → src/main/resources/static/js/dashboard.js
- `CertificationRepository` --references--> `Certification`  [EXTRACTED]
  src/main/java/com/certtrack/certification/CertificationRepository.java → src/main/java/com/certtrack/certification/Certification.java

## Import Cycles
- None detected.

## Communities (13 total, 3 thin omitted)

### Community 0 - "Certification API & Persistence"
Cohesion: 0.10
Nodes (15): jakarta.validation.constraints.AssertTrue, org.springframework.data.jpa.repository.JpaRepository, org.springframework.stereotype.Service, org.springframework.transaction.annotation.Transactional, CertificationController, CertificationRepository, CertificationRequest, CertificationResponse (+7 more)

### Community 1 - "Authentication & JWT"
Cohesion: 0.11
Nodes (18): io.jsonwebtoken.Claims, jakarta.servlet.FilterChain, javax.crypto.SecretKey, org.springframework.security.authentication.AuthenticationManager, org.springframework.security.core.userdetails.UserDetails, org.springframework.security.core.userdetails.UserDetailsService, org.springframework.web.bind.annotation.DeleteMapping, org.springframework.web.bind.annotation.PostMapping (+10 more)

### Community 2 - "Certification Domain Entity"
Cohesion: 0.13
Nodes (3): jakarta.persistence.Entity, Certification, BadRequestException

### Community 3 - "Error Handling"
Cohesion: 0.30
Nodes (11): jakarta.servlet.http.HttpServletRequest, org.springframework.http.HttpStatus, org.springframework.http.ResponseEntity, org.springframework.security.access.AccessDeniedException, org.springframework.security.authentication.BadCredentialsException, org.springframework.web.bind.annotation.ExceptionHandler, org.springframework.web.bind.annotation.RestControllerAdvice, org.springframework.web.bind.MethodArgumentNotValidException (+3 more)

### Community 4 - "Security Configuration"
Cohesion: 0.25
Nodes (9): org.springframework.context.annotation.Bean, org.springframework.context.annotation.Configuration, org.springframework.security.authentication.AuthenticationProvider, org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration, org.springframework.security.config.annotation.web.builders.HttpSecurity, org.springframework.security.config.annotation.web.configuration.EnableWebSecurity, org.springframework.security.crypto.password.PasswordEncoder, org.springframework.security.web.SecurityFilterChain (+1 more)

### Community 5 - "Frontend Dashboard (SPA)"
Cohesion: 0.22
Nodes (14): Dashboard UI (SPA), certifications, createCertification(), escapeHtml(), jwtToken, loadCertifications(), loginForm, loginStatus (+6 more)

### Community 6 - "JSON Auth Entry Point"
Cohesion: 0.33
Nodes (7): com.fasterxml.jackson.databind.ObjectMapper, jakarta.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException, org.springframework.security.web.AuthenticationEntryPoint, org.springframework.stereotype.Component, Override, JsonAuthenticationEntryPoint

### Community 7 - "Maven Wrapper Script"
Cohesion: 0.33
Nodes (6): mvnw script, clean(), die(), exec_maven(), set_java_home(), verbose()

### Community 8 - "Summary & Web Routing"
Cohesion: 0.29
Nodes (5): org.springframework.stereotype.Controller, org.springframework.web.bind.annotation.GetMapping, CertificationSummaryResponse, SkillCoverage, WebController

### Community 9 - "Integration Tests"
Cohesion: 0.60
Nodes (3): org.junit.jupiter.api.Test, org.springframework.boot.test.context.SpringBootTest, CertTrackApplicationTests

## Knowledge Gaps
- **10 isolated node(s):** `idea`, `com.certtrack:certtrack`, `ACTIVE`, `EXPIRING_SOON`, `EXPIRED` (+5 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **3 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CertificationController` connect `Certification API & Persistence` to `Summary & Web Routing`, `Authentication & JWT`, `Frontend Dashboard (SPA)`?**
  _High betweenness centrality (0.150) - this node is a cross-community bridge._
- **Why does `Dashboard UI (SPA)` connect `Frontend Dashboard (SPA)` to `Certification API & Persistence`, `Authentication & JWT`?**
  _High betweenness centrality (0.125) - this node is a cross-community bridge._
- **Why does `JwtService` connect `Authentication & JWT` to `Certification API & Persistence`?**
  _High betweenness centrality (0.124) - this node is a cross-community bridge._
- **What connects `idea`, `com.certtrack:certtrack`, `ACTIVE` to the rest of the system?**
  _10 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Certification API & Persistence` be split into smaller, more focused modules?**
  _Cohesion score 0.10256410256410256 - nodes in this community are weakly interconnected._
- **Should `Authentication & JWT` be split into smaller, more focused modules?**
  _Cohesion score 0.10756302521008404 - nodes in this community are weakly interconnected._
- **Should `Certification Domain Entity` be split into smaller, more focused modules?**
  _Cohesion score 0.12615384615384614 - nodes in this community are weakly interconnected._