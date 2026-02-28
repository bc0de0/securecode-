# Authentication Secure Coding Playbook

## 1. Overview
Secure authentication verifies and validates the identity of a platform user. This playbook ensures that our methods for establishing and maintaining user sessions are resilient to modern attacks.

## 2. Secure Practices

### 2.1 Password Security
- **Secure Hashing**: Use BCrypt with a cost factor of at least 12. Never store passwords in plaintext or using outdated algorithms (MD5, SHA1).
- **Strong Policies**: Enforce a minimum length of 12 characters and monitor for common/compromised passwords via HaveIBeenPwned API (planned).
- **Graceful Failures**: Return generic error messages (e.g., "Invalid email or password") to prevent username enumeration.

### 2.2 JWT Management
- **Asymmetric Signing**: Use RS256 with key rotation. Symmetric keys (HS256) are discouraged for multi-environment deployments.
- **Short Lifetimes**: Set token expiration to less than 1 hour. Use Refresh Tokens for long-term sessions, stored securely (e.g., HttpOnly cookies).
- **Claim Integrity**: Ensure sensitive claims like `tenantId` and `role` are immutable and verified on every request.

### 2.3 Brute Force & Session Defense
- **Account Lockout**: Implement temporary lockouts after multiple failed attempts to discourage automated brute-forcing.
- **MFA (Planned)**: Prepare for Multi-Factor Authentication (TOTP or WebAuthn) for administrative and analyst roles.
- **Session Revocation**: Maintain a blacklist or versioning for tokens to allow for immediate session invalidation in case of account compromise.

## 3. Implementation Checklist
- [ ] No passwords logged in application or error logs.
- [ ] BCrypt is confirmed as the primary password encoder.
- [ ] JWT `exp` [expiration] and `nbf` [not before] claims are enforced.
- [ ] Cookies are marked `Secure`, `HttpOnly`, and `SameSite=Strict`.
- [ ] Failed login attempts are audit-logged with the requester's IP.
