package com.securecode.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class Playbook {
    private String version;
    private String category;

    @JsonProperty("validation_rules")
    private List<PlaybookRule> validationRules;
}
