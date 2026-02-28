# Authorization Secure Coding Playbook

## 1. Overview
Authorization defines the permissions and access levels for an authenticated user. This playbook focuses on preventing unauthorized access to data and functions within the platform.

## 2. Secure Practices

### 2.1 Role-Based Access Control (RBAC)
- **Granular Controls**: Use `@PreAuthorize` with SpEL (Spring Expression Language) to restrict methods to specific roles (`USER`, `ANALYST`, `ADMIN`).
- **Administrative Constraints**: Administrative functions (e.g., user deletion, role changes) must be strictly reserved for the `ADMIN` role.
- **Fail-Safe Defaults**: Any resource not explicitly permitted to a role must be denied by default.

### 2.2 Multi-Tenant Isolation
- **Tenant Context**: The platform is inherently multi-tenant. Every user belongs to one tenant, identified by a `tenantId`.
- **Query Isolation**: All database queries must include a `WHERE tenant_id = :tenantId` clause. This ensures metadata and scan results are never shared across tenants.
- **Context Injection**: Use `SecurityUtils` or Spring Security's `Authentication principal` to dynamically retrieve the `tenantId` during business logic execution.

### 2.3 IDOR (Insecure Direct Object Reference) Prevention
- **Reference Checks**: When a user accesses a record by ID (e.g., `/projects/{id}`), the application must verify that the record belongs to the user's tenant before returning it.
- **Opaque References (Future)**: Consider using HASHIDs or obfuscated IDs for public-facing object references.

### 2.4 Broken Function Level Authorization (BFLA)
- **Consistency**: Ensure both UI and API layer enforce the same permission checks. An API endpoint must not be left unguarded even if the UI hides its button.
- **Uniform Authorization Patterns**: Implement authorization checks at the service layer to catch bypasses from direct API calls.

## 3. Implementation Checklist
- [ ] Every `@RestController` method has an `@PreAuthorize` annotation.
- [ ] No database query leaks data across `tenant_id` boundaries.
- [ ] All API responses are filter checked; only data the user is permitted to see is returned.
- [ ] Tests verify that a user from Tenant A cannot access resources from Tenant B.
