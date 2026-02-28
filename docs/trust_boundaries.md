# Trust Boundaries Documentation (Comprehensive)

## 1. Overview
This document defines the security boundaries of the SecureCode platform. A trust boundary is a perimeter where data moves between different levels of trust or ownership. Identifying these boundaries is critical for preventing Escalation of Privilege, Information Disclosure, and Spoofing attacks.

## 2. Active Trust Boundaries

### 2.1 Public API & Edge Boundary (Untrusted Workspace)
- **Description**: The primary entry point for all REST/GraphQL traffic.
- **Trust Level Shift**: External untrusted internet -> Authenticated Application Context.
- **Security Controls**:
  - **JWT Authentication**: Validating identity before any business logic executes.
  - **Rate Limiting (Bucket4j)**: Preventing resource exhaustion/DoS.
  - **Input Validation (JSR-303/Jakarta)**: Strict enforcement of schemas for `@RequestBody` and `@PathVariable`.
  - **CORS Policies**: Restricting allowed origins for browser-based clients.

### 2.2 GitHub Repository Boundary (External Code Access)
- **Description**: Occurs when the application clones external repositories via JGit based on user-provided strings.
- **Trust Level Shift**: Application Logic -> External Remote Git Host.
- **Security Controls**:
  - **Ephemeral Filesystem Isolation**: Each clone happens in a unique, non-executable temporary directory.
  - **No-Code Execution**: The platform only performs static analysis; it never compiles or executes the target repository code.
  - **Timeout Enforcement**: Preventing "slow clone" or large repo attacks from hanging application threads.

### 2.3 Local Process Execution Boundary (SAST/SCA Engines)
- **Description**: The boundary between the Java JVM and external binaries like `semgrep` or future tools like `dependency-check`.
- **Trust Level Shift**: Java Runtime -> Local OS Shell Environment.
- **Security Controls**:
  - **Parameterization**: Arguments are passed via `List<String>` to `ProcessBuilder`, avoiding shell metadata injection.
  - **Output Redirection**: Standard output and error are piped to controlled temp files rather than being read into memory buffers directly (preventing deadlocks/memory spikes).

### 2.4 Multi-Tenant Boundary (Data Sequestration)
- **Description**: Isolation between logical tenants sharing the same physical infrastructure.
- **Trust Level Shift**: Shared System Process -> Tenant-Specific Data Context.
- **Security Controls**:
  - **Tenant Filtering**: All DAO/Repository queries are hard-coded to include `tenant_id` filters.
  - **Security Context ThreadLocal**: The platform maintains the current `tenantId` in the security context for consistent enforcement.

## 3. Planned Extensions & Future Boundaries

### 3.1 AI Service Boundary (LLM Provider Context)
- **Planned Feature**: Integration with OpenAI, Anthropic, or Gemini for vulnerability summarization and remediation.
- **Trust Level Shift**: SecureCode Environment -> Third-Party AI Provider Cloud.
- **Security Risk**: Data exfiltration of proprietary source code snippets to the AI provider.
- **Mitigation Plan**: 
  - **Snippet Redaction**: Identifying and masking PII/Secrets before sending code to AI.
  - **Strict API Versioning**: Using enterprise-tier AI endpoints with non-training guarantees.

### 3.2 SCA Vulnerability Database Boundary (Software Composition)
- **Planned Feature**: Analyzing `pom.xml` / `package.json` for known CVEs.
- **Trust Level Shift**: Application -> Central Vulnerability Repositories (NVD, OSV.dev).
- **Security Risk**: Integrity of the vulnerability data fetched over the network.
- **Mitigation Plan**: TLS certificate pinning and checksum verification for local vulnerability database updates.

### 3.3 Outgoing Webhook & Integration Boundary (Jira/GitHub)
- **Planned Feature**: Automatic ticket creation and PR comments.
- **Trust Level Shift**: SecureCode Result -> External Corporate Task Trackers.
- **Security Risk**: SSRF (Server Side Request Forgery) if users can define webhook targets.
- **Mitigation Plan**: Whitelisting allowed domains and using a dedicated egress proxy.

### 3.4 Framework & Library Trust Boundary
- **Description**: Trust established in Spring Boot, Hibernate, and the JVM itself.
- **Security Hardening**:
  - **Dependency Hardening**: Regular scanning of the platform's own `pom.xml`.
  - **JVM Sandboxing**: Restricting file system access for analysis threads using Java Security Manager (if available) or OS-level container isolation.
  - **Secure Defaults**: Disabling unnecessary framework features (e.g., Spring Actuator sensitive endpoints).
