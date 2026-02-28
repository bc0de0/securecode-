package com.securecode.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class PlaybookValidator {

    private final Map<ValidationDomain, Playbook> playbooks = new EnumMap<>(ValidationDomain.class);
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    @PostConstruct
    public void init() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath:playbooks/*.yaml");
            for (Resource resource : resources) {
                Playbook playbook = mapper.readValue(resource.getInputStream(), Playbook.class);
                ValidationDomain domain = determineDomain(resource.getFilename());
                if (domain != null) {
                    playbooks.put(domain, playbook);
                    log.info("Loaded playbook for domain: {} with {} rules", domain,
                            playbook.getValidationRules().size());
                }
            }
        } catch (IOException e) {
            log.error("Failed to load playbooks", e);
        }
    }

    private ValidationDomain determineDomain(String filename) {
        for (ValidationDomain domain : ValidationDomain.values()) {
            if (domain.getFileName().equalsIgnoreCase(filename)) {
                return domain;
            }
        }
        return null;
    }

    public ValidationResult validate(ValidationDomain domain, String input) {
        Playbook playbook = playbooks.get(domain);
        if (playbook == null) {
            return ValidationResult.builder()
                    .valid(true)
                    .domain(domain.name())
                    .violations(Collections.emptyList())
                    .build();
        }

        List<String> violations = new ArrayList<>();
        // Simple validation logic based on rule description matching
        // In a real scenario, this would be more sophisticated (e.g., calling an LLM or
        // specialized scanners)
        for (PlaybookRule rule : playbook.getValidationRules()) {
            if (isViolated(rule, input)) {
                violations.add(rule.getId() + ": " + rule.getRule());
            }
        }

        return ValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .domain(domain.name())
                .build();
    }

    private boolean isViolated(PlaybookRule rule, String input) {
        // Mocking some simple checks for the demo
        String ruleText = rule.getRule().toLowerCase();
        String inputLower = input.toLowerCase();

        if (ruleText.contains("block") || ruleText.contains("identify")) {
            if (rule.getId().equals("VAL_AI_01")
                    && (inputLower.contains("ignore previous instructions") || inputLower.contains("now acting as"))) {
                return true;
            }
        }

        if (rule.getId().equals("VAL-AUTH-03") && inputLower.contains("alg: none")) {
            return true;
        }

        // Add more logic as needed for other domains
        return false;
    }

    public List<PlaybookRule> getRules(ValidationDomain domain) {
        Playbook playbook = playbooks.get(domain);
        return playbook != null ? playbook.getValidationRules() : Collections.emptyList();
    }
}
