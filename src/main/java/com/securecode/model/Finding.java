package com.securecode.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "findings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Finding {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private AnalysisRequest request;

    @Column(name = "rule_id", nullable = false)
    private String ruleId;

    @Column(nullable = false)
    private String severity;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "line_number")
    private Integer lineNumber;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
}
