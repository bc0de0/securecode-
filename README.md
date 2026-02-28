# SecureCode

SecureCode is an enterprise-grade AI-Assisted Code Analysis and DevSecOps Control Plane built with Spring Boot 3 and Java 21. It provides a robust, multi-tenant platform for scanning codebases and validating AI suggestions against security playbooks.

## 🚀 Key Features

- **Security Validator Layer**: Validates code and prompts against domain-specific security playbooks (AI, Auth, Authorization, Config, Input).
- **AI-Assisted Analysis**: Integrates with LLMs to provide intelligent security remediation and findings summaries.
- **Multi-Tenant Isolation**: Ensures secure data separation across different organizational units.
- **Static Analysis (SAST)**: Extensible scanner layer using industry-standard tools for identifying vulnerabilities.
- **Real-time Monitoring**: Built-in audit logging and health check systems.

## 📁 Project Structure

```text
src/main/java/com/securecode/
├── config/       # Security and operational configurations
├── controller/   # REST API Endpoints (including /validator)
├── dto/          # Data Transfer Objects
├── model/        # JPA Entities and domain models
├── repository/   # Data Access Layer
├── scanner/      # Integration with SAST tools
├── security/     # RBAC and authentication logic
├── service/      # Business logic implementation
└── validator/    # Security playbook validation middleware
```

## 🛠️ Getting Started

### Prerequisites

- **Java 21**
- **Maven** (included via `./mvnw`)
- **Docker** (for PostgreSQL and development infrastructure)

### Building the Project

Run the Maven wrapper to build the application:

```bash
./mvnw clean compile
```

### Running Locally

To start the control plane locally:

```bash
./mvnw spring-boot:run
```

## 🔒 Security Playbooks

Security playbooks are defined in YAML under `src/main/resources/playbooks/` and cover:
- **AI**: Protection against prompt injection and token limits.
- **Auth**: Strong password requirements and JWT algorithm restrictions.
- **Authorization**: RBAC and multi-tenant isolation rules.
- **Config**: Secure environment configurations and secrets management.
- **Input**: Hardened validation rules for user-controlled data.

---
*Developed under the SecureCode Initiative*
