package com.securecode.controller;

import com.securecode.dto.AnalysisRequestDTO;
import com.securecode.model.enums.AnalysisType;
import com.securecode.service.AnalysisService;
import com.securecode.service.RateLimitService;
import com.securecode.util.SecurityUtils;
import io.github.bucket4j.Bucket;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;
    private final RateLimitService rateLimitService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<AnalysisRequestDTO> submitAnalysis(@Valid @RequestBody AnalysisRequestDTO dto) {
        return handleRequest(dto);
    }

    @PostMapping("/url")
    @PreAuthorize("hasAnyRole('USER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<AnalysisRequestDTO> submitUrlAnalysis(@RequestParam UUID projectId,
            @RequestParam String url) {
        AnalysisRequestDTO dto = AnalysisRequestDTO.builder()
                .projectId(projectId)
                .type(AnalysisType.GITHUB_REPO)
                .inputPayload(url)
                .build();
        return handleRequest(dto);
    }

    @PostMapping("/docs")
    @PreAuthorize("hasAnyRole('USER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<AnalysisRequestDTO> submitDocsContext(@RequestParam UUID projectId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String content = new String(file.getBytes());
        AnalysisRequestDTO dto = AnalysisRequestDTO.builder()
                .projectId(projectId)
                .type(AnalysisType.DOCUMENTATION)
                .inputPayload(content)
                .build();
        return handleRequest(dto);
    }

    @PostMapping("/ai")
    @PreAuthorize("hasAnyRole('USER', 'ANALYST', 'ADMIN')")
    public ResponseEntity<AnalysisRequestDTO> submitUserAiContext(@RequestParam UUID projectId,
            @RequestBody String userContext) {
        AnalysisRequestDTO dto = AnalysisRequestDTO.builder()
                .projectId(projectId)
                .type(AnalysisType.MANUAL_CONTEXT)
                .inputPayload(userContext)
                .build();
        return handleRequest(dto);
    }

    private ResponseEntity<AnalysisRequestDTO> handleRequest(AnalysisRequestDTO dto) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        Bucket bucket = rateLimitService.resolveBucket(tenantId);

        if (bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(analysisService.submitAnalysis(dto));
        } else {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
    }
}
