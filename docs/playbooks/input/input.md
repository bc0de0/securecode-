# Input Vulnerability Secure Coding Playbook

## 1. Overview
Input validation is our first line of defense against attacks that leverage untrusted data to manipulate application logic or the underlying infrastructure. This playbook defines strategies for preventing the most common input-related vulnerabilities.

## 2. Core Strategies

### 2.1 Injection Prevention (SQL, Command, LDAP)
- **Parameterized Queries**: Never concatenate strings to build SQL or any other command. Use prepared statements and ORM integrations (Hibernate/JPA).
- **Command Parameterization**: Use `ProcessBuilder` with array/list arguments rather than raw shell strings to prevent shell metadata injection.
- **Whitelist Validation**: If a dynamic query is unavoidable, validate that the input matches exactly a whitelist of allowed field names or identifiers.

### 2.2 Path Traversal & LFI
- **Sanitize Paths**: Never allow `..`, `/`, or `\` in user-provided file names.
- **Rooted Directories**: Always normalize paths using `Path.toAbsolutePath().normalize()` and verify they start with the expected base directory.
- **Randomized File Names**: Store uploaded files using a generated UUID rather than the user's provided filename.

### 2.3 Cross-Site Scripting (XSS)
- **Output Encoding**: Use modern templating engines (e.g., React, Thymeleaf) that handle output encoding by default.
- **Explicit Sanitization**: If raw HTML must be rendered, use a trusted library like `jsoup` with a strict whitelist policy.
- **CSP Headers**: Implement a Content Security Policy (CSP) to restrict allowed scripts.

### 2.4 Data Truncation and Type Checks
- **Length Limits**: Use `@Size` constraints on all string fields to prevent potential memory exhaustion or buffer-related logic flaws.
- **Strict Casting**: Verify types (e.g., Integer vs. String) at the earliest possible entry point to the system.

## 3. Implementation Checklist
- [ ] All `@RestController` methods use `@Valid` or `@Validated`.
- [ ] No manual SQL string concatenation in repositories.
- [ ] Path manipulation logic uses the `java.nio.file.Path` API exclusively.
- [ ] XSS filters are enabled at the gateway/proxy level.
