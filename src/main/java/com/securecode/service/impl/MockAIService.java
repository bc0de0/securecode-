package com.securecode.service.impl;

import com.securecode.model.enums.RiskLevel;
import com.securecode.dto.AnalysisResultDTO;
import com.securecode.dto.FindingDTO;
import com.securecode.service.AIService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MockAIService implements AIService {

    @Override
    public AnalysisResultDTO summarizeFindings(List<FindingDTO> findings, String contextDocs, String userAiContext) {
        StringBuilder summaryBuilder = new StringBuilder();

        if (findings == null || findings.isEmpty()) {
            summaryBuilder.append("No critical findings detected. ");
            if (contextDocs != null && !contextDocs.isBlank()) {
                summaryBuilder.append(
                        "Context from project documentation was reviewed, and no immediate contradictions were found. ");
            }
            return AnalysisResultDTO.builder()
                    .riskLevel(RiskLevel.LOW)
                    .summary(summaryBuilder.toString())
                    .aiConfidenceScore(0.95)
                    .findingsSummaryJson("{}")
                    .build();
        }

        long highCount = findings.stream().filter(f -> "ERROR".equalsIgnoreCase(f.getSeverity())).count();
        RiskLevel risk = highCount > 0 ? RiskLevel.HIGH : RiskLevel.MEDIUM;

        summaryBuilder.append("AI Analysis identified ").append(findings.size()).append(" potential security issues. ");

        if (userAiContext != null && !userAiContext.isBlank()) {
            summaryBuilder.append("User-provided context was used to prioritize results: '")
                    .append(userAiContext.length() > 50 ? userAiContext.substring(0, 47) + "..." : userAiContext)
                    .append("'. ");
        }

        if (contextDocs != null && !contextDocs.isBlank()) {
            summaryBuilder.append("Project security notes were integrated into the assessment. ");
        }

        summaryBuilder.append("Concentration of high severity issues suggests manual review is required.");

        return AnalysisResultDTO.builder()
                .riskLevel(risk)
                .summary(summaryBuilder.toString())
                .aiConfidenceScore(0.88)
                .findingsSummaryJson("{\"total\":" + findings.size() + ",\"critical\":" + highCount + "}")
                .build();
    }
}
