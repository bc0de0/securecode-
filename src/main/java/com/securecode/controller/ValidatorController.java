package com.securecode.controller;

import com.securecode.validator.PlaybookRule;
import com.securecode.validator.PlaybookValidator;
import com.securecode.validator.ValidationDomain;
import com.securecode.validator.ValidationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/validator")
@RequiredArgsConstructor
@Tag(name = "Security Validator", description = "Validate suggestions and code against security playbooks")
public class ValidatorController {

    private final PlaybookValidator playbookValidator;

    @PostMapping("/validate/{domain}")
    @Operation(summary = "Validate input against a specific security domain playbook")
    public ResponseEntity<ValidationResult> validate(
            @PathVariable ValidationDomain domain,
            @RequestBody Map<String, String> request) {

        String input = request.getOrDefault("input", "");
        ValidationResult result = playbookValidator.validate(domain, input);

        if (!result.isValid()) {
            return ResponseEntity.badRequest().body(result);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/rules/{domain}")
    @Operation(summary = "Get all validation rules for a security domain")
    public ResponseEntity<List<PlaybookRule>> getRules(@PathVariable ValidationDomain domain) {
        return ResponseEntity.ok(playbookValidator.getRules(domain));
    }
}
