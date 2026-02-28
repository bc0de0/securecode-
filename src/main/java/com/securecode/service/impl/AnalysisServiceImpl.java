package com.securecode.service.impl;

import com.securecode.dto.AnalysisRequestDTO;
import com.securecode.dto.AnalysisResultDTO;
import com.securecode.dto.FindingDTO;
import com.securecode.model.AnalysisRequest;
import com.securecode.model.AnalysisResult;
import com.securecode.model.Finding;
import com.securecode.model.Project;
import com.securecode.model.enums.AnalysisStatus;
import com.securecode.model.enums.AnalysisType;
import com.securecode.repository.AnalysisRequestRepository;
import com.securecode.repository.AnalysisResultRepository;
import com.securecode.repository.FindingRepository;
import com.securecode.repository.ProjectRepository;
import com.securecode.scanner.ScannerAdapter;
import com.securecode.service.AIService;
import com.securecode.service.AnalysisService;
import com.securecode.service.AuditService;
import com.securecode.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisServiceImpl implements AnalysisService {

    private final AnalysisRequestRepository requestRepository;
    private final ProjectRepository projectRepository;
    private final FindingRepository findingRepository;
    private final AnalysisResultRepository resultRepository;
    private final ScannerAdapter scannerAdapter;
    private final AIService aiService;
    private final AuditService auditService;

    @Override
    @Transactional
    public AnalysisRequestDTO submitAnalysis(AnalysisRequestDTO dto) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        UUID userId = SecurityUtils.getCurrentUserId();

        Project project = projectRepository.findByIdAndTenantId(dto.getProjectId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found or access denied"));

        AnalysisRequest request = AnalysisRequest.builder()
                .project(project)
                .submittedBy(userId)
                .type(dto.getType())
                .inputPayload(dto.getInputPayload())
                .status(AnalysisStatus.PENDING)
                .build();

        request = requestRepository.save(request);

        auditService.log(userId, "ANALYSIS_SUBMISSION", "AnalysisRequest", request.getId(),
                Map.of("project_id", project.getId(), "type", dto.getType()));

        if (dto.getType() == AnalysisType.DOCUMENTATION) {
            project.setContextDocs(dto.getInputPayload());
            projectRepository.save(project);
            request.setStatus(AnalysisStatus.COMPLETED);
            requestRepository.save(request);
        } else if (dto.getType() == AnalysisType.MANUAL_CONTEXT) {
            project.setAiUserContext(dto.getInputPayload());
            projectRepository.save(project);
            request.setStatus(AnalysisStatus.COMPLETED);
            requestRepository.save(request);
        } else {
            processAnalysisAsync(request.getId());
        }

        return mapToDTO(request);
    }

    @Override
    @Async("analysisTaskExecutor")
    @Transactional
    public void processAnalysisAsync(UUID requestId) {
        AnalysisRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found: " + requestId));

        Project project = request.getProject();
        try {
            log.info("Starting async analysis for request: {} (Type: {})", requestId, request.getType());

            List<FindingDTO> findings = scannerAdapter.scan(request.getInputPayload(), request.getType());
            List<Finding> findingEntities = findings.stream().map(f -> Finding.builder()
                    .request(request)
                    .ruleId(f.getRuleId())
                    .severity(f.getSeverity())
                    .filePath(f.getFilePath())
                    .lineNumber(f.getLineNumber())
                    .message(f.getMessage())
                    .build()).collect(Collectors.toList());

            findingRepository.saveAll(findingEntities);
            request.setStatus(AnalysisStatus.SCANNED);
            requestRepository.save(request);

            auditService.log(request.getSubmittedBy(), "SCANNER_EXECUTION", "AnalysisRequest", request.getId(),
                    Map.of("findings_count", findings.size()));

            AnalysisResultDTO aiResultDTO = aiService.summarizeFindings(findings, project.getContextDocs(),
                    project.getAiUserContext());
            AnalysisResult resultEntity = AnalysisResult.builder()
                    .request(request)
                    .riskLevel(aiResultDTO.getRiskLevel())
                    .summary(aiResultDTO.getSummary())
                    .aiConfidenceScore(aiResultDTO.getAiConfidenceScore())
                    .findingsSummaryJson(aiResultDTO.getFindingsSummaryJson())
                    .build();

            resultRepository.save(resultEntity);
            request.setStatus(AnalysisStatus.COMPLETED);
            requestRepository.save(request);
            log.info("Analysis request {} completed successfully", requestId);

        } catch (Exception e) {
            log.error("Failed to process analysis request {}", request.getId(), e);
            request.setStatus(AnalysisStatus.FAILED);
            requestRepository.save(request);
        }
    }

    private AnalysisRequestDTO mapToDTO(AnalysisRequest request) {
        return AnalysisRequestDTO.builder()
                .id(request.getId())
                .projectId(request.getProject().getId())
                .type(request.getType())
                .inputPayload("[REDACTED]")
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .submittedBy(request.getSubmittedBy())
                .build();
    }
}
