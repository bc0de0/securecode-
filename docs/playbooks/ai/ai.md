# AI Secure Coding Playbook

## 1. Overview
Integrating Artificial Intelligence (AI) and Large Language Models (LLMs) into SecureCode introduces unique risks related to data privacy, prompt manipulation, and unreliable machine outputs. This playbook defines how we build, deploy, and manage AI services securely.

## 2. Secure Practices

### 2.1 Prompt Injection Prevention
- **Strict Context Isolation**: Clearly separate the "System Instructions" from the "User Input" in all LLM calls.
- **Instruction Reinforcement**: Repeatedly instruct the LLM to ignore overrides that attempt to derail its purpose.
- **Input Filtering**: Filter user-submitted code snippets for common injection keywords or phrases that manipulate model behavior.

### 2.2 Data Privacy & PII Protection
- **Snippet Redaction**: Before sending any user code to the AI provider, the application must identify and mask PII (Emails, Names, Addresses) and Secrets (API Keys, JWTs, Passwords).
- **Vendor Configuration**: Use enterprise settings on AI providers that explicitly disable model training on user data.

### 2.3 Management of AI Hallucinations
- **Human-in-the-Loop**: Always display AI results (e.g., remediation advice) as "AI-Generated Suggestions" that require manual developer verification.
- **Confidence Scoring**: Require AI models to return a confidence score or 'certainty' level to help identify lower-quality hallucinations.

### 2.4 Prompt Logging & Governance
- **AI Audit Trail**: Maintain a separate log for every prompt and its corresponding AI response. This allows for post-incident analysis of biased or erroneous LLM behaviors.
- **Cost Controls**: Implement per-tenant token limits to prevent "wallet-draining" attacks where a malicious user triggers massive, expensive LLM calls.

## 3. Implementation Checklist
- [ ] No user-provided input is ever placed directly into the "System instruction" part of a prompt.
- [ ] LLM provider settings are confirmed to exclude training on submitted data.
- [ ] Redaction filter is applied as the final step before the AI network call.
- [ ] AI results are explicitly labeled as "unverified" in the UI.
- [ ] Prompts and Responses are audit-logged for governance.
