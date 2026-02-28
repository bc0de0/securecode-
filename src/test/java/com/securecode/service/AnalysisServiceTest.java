package com.securecode.service;

import com.securecode.dto.AnalysisRequestDTO;
import com.securecode.dto.AnalysisResultDTO;
import com.securecode.dto.FindingDTO;
import com.securecode.model.AnalysisRequest;
import com.securecode.model.AnalysisResult;
import com.securecode.model.Finding;
import com.securecode.model.Project;
import com.securecode.model.enums.AnalysisStatus;
import com.securecode.model.enums.AnalysisType;
import com.securecode.model.enums.RiskLevel;
import com.securecode.repository.AnalysisRequestRepository;
import com.securecode.repository.AnalysisResultRepository;
import com.securecode.repository.FindingRepository;
import com.securecode.repository.ProjectRepository;
import com.securecode.scanner.ScannerAdapter;
import com.securecode.service.impl.AnalysisServiceImpl;
import com.securecode.util.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

    @Mock
    private AnalysisRequestRepository requestRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private FindingRepository findingRepository;
    @Mock
    private AnalysisResultRepository resultRepository;
    @Mock
    private ScannerAdapter scannerAdapter;
    @Mock
    private AIService aiService;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private AnalysisServiceImpl analysisService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private UUID tenantId;
    private UUID userId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(SecurityUtils::getCurrentTenantId).thenReturn(tenantId);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void testSubmitAnalysis_Success() {
        AnalysisRequestDTO dto = AnalysisRequestDTO.builder()
                .projectId(projectId)
                .type(AnalysisType.CODE_SNIPPET)
                .inputPayload("print('hello')")
                .build();

        Project project = Project.builder().id(projectId).build();
        when(projectRepository.findByIdAndTenantId(projectId, tenantId)).thenReturn(Optional.of(project));

        AnalysisRequest savedRequest = AnalysisRequest.builder()
                .id(UUID.randomUUID())
                .project(project)
                .submittedBy(userId)
                .type(dto.getType())
                .inputPayload(dto.getInputPayload())
                .status(AnalysisStatus.PENDING)
                .build();

        when(requestRepository.save(any(AnalysisRequest.class))).thenReturn(savedRequest);

        // Mock scanner and AI interaction
        List<FindingDTO> findings = List.of(FindingDTO.builder().ruleId("RULE-01").severity("HIGH").build());
        when(scannerAdapter.scan(anyString(), any(AnalysisType.class))).thenReturn(findings);

        AnalysisResultDTO aiResult = AnalysisResultDTO.builder()
                .riskLevel(RiskLevel.HIGH)
                .summary("Vulnerable code discovered")
                .aiConfidenceScore(0.9)
                .build();
        when(aiService.summarizeFindings(anyList(), any(), any())).thenReturn(aiResult);

        AnalysisRequestDTO result = analysisService.submitAnalysis(dto);

        assertNotNull(result);
        verify(requestRepository, atLeastOnce()).save(any(AnalysisRequest.class));
        // Note: verify findingRepository.saveAll and resultRepository.save will happen
        // in async method in real impl,
        // but here we are testing partial/initial flow if it's not
        // DOCUMENTATION/MANUAL_CONTEXT.
    }

    @Test
    void testSubmitAnalysis_ProjectNotFound() {
        AnalysisRequestDTO dto = AnalysisRequestDTO.builder()
                .projectId(projectId)
                .type(AnalysisType.CODE_SNIPPET)
                .build();

        when(projectRepository.findByIdAndTenantId(projectId, tenantId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> analysisService.submitAnalysis(dto));
    }
}
