package com.securecode.validator;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ValidationResult {
    private boolean valid;
    private List<String> violations;
    private String domain;
}
