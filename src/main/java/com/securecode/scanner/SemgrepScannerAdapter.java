package com.securecode.scanner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.securecode.model.enums.AnalysisType;
import com.securecode.dto.FindingDTO;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SemgrepScannerAdapter implements ScannerAdapter {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final long MAX_EXECUTION_TIME_SECONDS = 300; // Increased for repo scanning
    private static final long MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024; // 5MB limit for snippets

    @Override
    public List<FindingDTO> scan(String input, AnalysisType type) {
        if (input == null || input.isBlank()) {
            log.warn("Scan input is null or blank");
            return List.of();
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("semgrep_scan_" + UUID.randomUUID());
            Path scanTarget;

            if (type == AnalysisType.GITHUB_REPO) {
                log.info("Cloning repository: {} to {}", input, tempDir);
                Git.cloneRepository()
                        .setURI(input)
                        .setDirectory(tempDir.toFile())
                        .setCloneSubmodules(false)
                        .call();
                scanTarget = tempDir;
            } else {
                if (input.length() > MAX_FILE_SIZE_BYTES) {
                    log.warn("Code snippet size exceeds limit");
                    return List.of();
                }
                Path codeFile = tempDir.resolve("input_code.java");
                Files.writeString(codeFile, input);
                scanTarget = codeFile;
            }

            return executeSemgrep(scanTarget, type);

        } catch (Exception e) {
            log.error("Error during semgrep execution/cloning", e);
            return List.of();
        } finally {
            if (tempDir != null) {
                cleanup(tempDir.toFile());
            }
        }
    }

    private List<FindingDTO> executeSemgrep(Path target, AnalysisType type) {
        Path stdoutFile = null;
        Path stderrFile = null;
        try {
            stdoutFile = Files.createTempFile("semgrep_out", ".json");
            stderrFile = Files.createTempFile("semgrep_err", ".log");

            List<String> command = new ArrayList<>();
            command.add("semgrep");
            command.add("scan");

            if (type == AnalysisType.SCA_SCAN) {
                command.add("--config");
                command.add("p/security-audit");
            } else {
                command.add("--config");
                command.add("auto");
            }

            command.add("--json");
            command.add(target.toString());

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(stdoutFile.toFile());
            pb.redirectError(stderrFile.toFile());

            log.info("Executing semgrep: {}", String.join(" ", command));
            Process process = pb.start();

            boolean finished = process.waitFor(MAX_EXECUTION_TIME_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                log.error("Semgrep scan timed out");
                return List.of();
            }

            String stdout = Files.readString(stdoutFile);
            if (process.exitValue() != 0 && stdout.isBlank()) {
                String stderr = Files.readString(stderrFile);
                log.error("Semgrep exited with error code {}: {}", process.exitValue(), stderr);
                return List.of();
            }

            return parseSemgrepOutput(stdout);

        } catch (IOException | InterruptedException e) {
            log.error("Failed to execute semgrep process", e);
            return List.of();
        } finally {
            try {
                if (stdoutFile != null)
                    Files.deleteIfExists(stdoutFile);
                if (stderrFile != null)
                    Files.deleteIfExists(stderrFile);
            } catch (IOException e) {
                log.warn("Failed to delete temp files: {}", e.getMessage());
            }
        }
    }

    private List<FindingDTO> parseSemgrepOutput(String jsonOutput) {
        List<FindingDTO> findings = new ArrayList<>();
        if (jsonOutput == null || jsonOutput.isBlank()) {
            return findings;
        }
        try {
            JsonNode root = objectMapper.readTree(jsonOutput);
            JsonNode results = root.path("results");
            if (results.isArray()) {
                for (JsonNode node : results) {
                    findings.add(FindingDTO.builder()
                            .ruleId(node.path("check_id").asText())
                            .severity(node.path("extra").path("severity").asText())
                            .filePath(node.path("path").asText())
                            .lineNumber(node.path("start").path("line").asInt())
                            .message(node.path("extra").path("message").asText())
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse semgrep output", e);
        }
        return findings;
    }

    private void cleanup(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                cleanup(f);
            }
        }
        if (!file.delete()) {
            log.warn("Failed to delete temp file: {}", file.getAbsolutePath());
        }
    }
}
