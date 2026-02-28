package com.securecode.dto;

import com.securecode.model.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResultDTO {
    private UUID id;
    private UUID requestId;
    private RiskLevel riskLevel;
    private String summary;
    private Double aiConfidenceScore;
    private String findingsSummaryJson;
}
