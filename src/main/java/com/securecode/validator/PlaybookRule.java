package com.securecode.validator;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlaybookRule {
    private String id;

    @JsonProperty("field_type")
    private String fieldType;

    private String rule;
    private String enforcement;
}
