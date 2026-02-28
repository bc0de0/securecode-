package com.securecode.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ProjectDTO {
    private UUID id;

    @NotBlank
    private String name;

    private String description;
    private String contextDocs;
    private String aiUserContext;

    private UUID tenantId;
    private UUID createdBy;
    private LocalDateTime createdAt;
}
