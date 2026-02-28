package com.securecode.dto;

import com.securecode.model.enums.AnalysisStatus;
import com.securecode.model.enums.AnalysisType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequestDTO {
    private UUID id;

    @NotNull
    private UUID projectId;

    @NotNull
    private AnalysisType type;

    @NotBlank
    @Size(max = 1000000) // 1MB limit for code submission
    private String inputPayload;

    private AnalysisStatus status;
    private LocalDateTime createdAt;
    private UUID submittedBy;
}
