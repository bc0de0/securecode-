package com.securecode.model;

import com.securecode.model.enums.AnalysisStatus;
import com.securecode.model.enums.AnalysisType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analysis_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "submitted_by", nullable = false)
    private UUID submittedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisType type;

    @Column(name = "input_payload", columnDefinition = "TEXT", nullable = false)
    private String inputPayload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = AnalysisStatus.PENDING;
        }
    }
}
