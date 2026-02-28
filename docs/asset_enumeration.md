# Asset Enumeration (Comprehensive)

## 1. Overview
This document enumerates all valuable assets within the SecureCode platform that require protection. This includes data, infrastructure, and runtime components.

## 2. Core Security & Identity Assets

### 2.1 Passwords (BCrypt Hashes)
- **Description**: User login credentials stored in the `users` table.
- **Protection**: Hash only, salt automatically applied by BCrypt (Standard 10-12 rounds).
- **Criticality**: HIGH (Confidentiality).

### 2.2 JWT Private Keys (RS256)
- **Description**: The asymmetric RSA-256 private key used for signing session tokens.
- **Protection**: Loaded from an encrypted secret store (Planned: Azure Key Vault/AWS Secrets Manager).
- **Criticality**: CRITICAL (Integrity/Confidentiality).

### 2.3 User Session Tokens (JWT)
- **Description**: Short-lived claims (15-60m) authorizing user access.
- **Protection**: Signed, including `tenantId` and `userId` claims. Not stored in the DB (Stateless).
- **Criticality**: HIGH (Confidentiality).

## 3. Application Data Assets

### 3.1 Cloned Source Code (External)
- **Description**: Files cloned from user repositories for static analysis.
- **Protection**: Stored in ephemeral `/tmp` folders. Immediately deleted post-analysis. 
- **Criticality**: CRITICAL (Confidentiality/Copyright). *Never persist beyond the scan process.*

### 3.2 Vulnerability Findings & Risk Reports
- **Description**: Semgrep output and AI-generated risk summaries.
- **Protection**: Row-level tenant isolation in `findings` and `analysis_results` tables.
- **Criticality**: HIGH (Confidentiality/Integrity).

### 3.3 Audit Logs
- **Description**: History of all system activities (logins, scans, role changes).
- **Protection**: Appended-only storage, serialized as JSON with tamper-evident actor IDs.
- **Criticality**: MEDIUM (Integrity/Traceability).

## 4. Future Planned Assets (Sprint-Ready)

### 4.1 SCA Vulnerability Database (Cached)
- **Description**: Local copies of CVE metadata from NVD or OSV for faster SCA scanning.
- **Protection**: Read-only local database mirroring. Integrity checks on update.
- **Criticality**: MEDIUM (Availability/Integrity).

### 4.2 AI Context & Prompt Templates
- **Description**: The system prompts used to command LLMs for security analysis.
- **Protection**: Stored as code/resources; protected from "Prompt Injection" via strict input filtering.
- **Criticality**: MEDIUM (Confidentiality/Intellectual Property).

### 4.3 Integration Secrets (API Keys)
- **Description**: GitHub App tokens, Jira API credentials, Slack Webhook URLs.
- **Protection**: Encrypted at rest using a per-tenant master key (Planned).
- **Criticality**: HIGH (Confidentiality/Account Takeover).

## 5. System & Runtime Assets (Infrastructure)

### 5.1 JVM Runtime & Classpath
- **Description**: The Spring Boot JAR and all its direct/transitive dependencies.
- **Security Check**: The platform must scan its own dependencies for vulnerabilities (Self-SCA).
- **Criticality**: HIGH (Integrity/Availability).

### 5.2 PostgreSQL Persistence
- **Description**: The relational engine storing all configuration and metadata.
- **Protection**: TLS-encrypted connections (mtls), restricted database users (App User vs Flyway User).
- **Criticality**: CRITICAL (Integrity/Availability).

### 5.3 Static Analysis Binaries (Semgrep/Others)
- **Description**: Local OS binaries executed as part of the pipeline.
- **Protection**: Path-restricted execution; ensuring no user-provided binary can be run.
- **Criticality**: MEDIUM (Integrity).

### 5.4 Application Configuration (application.yml)
- **Description**: Environment variables, DB credentials, and tuning parameters.
- **Protection**: Injected via Docker environment variables or K8s Secrets.
- **Criticality**: HIGH (Confidentiality).
