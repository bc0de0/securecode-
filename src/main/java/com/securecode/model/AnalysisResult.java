package com.securecode.model;

import com.securecode.model.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "analysis_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private AnalysisRequest request;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(name = "ai_confidence_score")
    private Double aiConfidenceScore;

    @Column(name = "findings_summary_json", columnDefinition = "TEXT")
    private String findingsSummaryJson;
}
