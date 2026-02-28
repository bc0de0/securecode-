package com.securecode.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindingDTO {
    private UUID id;
    private UUID requestId;
    private String ruleId;
    private String severity;
    private String filePath;
    private Integer lineNumber;
    private String message;
}
