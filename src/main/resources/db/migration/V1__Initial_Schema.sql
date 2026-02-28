CREATE TABLE tenants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    tenant_id UUID NOT NULL REFERENCES tenants(id)
);

CREATE TABLE projects (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE analysis_requests (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id),
    submitted_by UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    input_payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE findings (
    id UUID PRIMARY KEY,
    request_id UUID NOT NULL REFERENCES analysis_requests(id),
    rule_id VARCHAR(255) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    file_path VARCHAR(1024),
    line_number INTEGER,
    message TEXT NOT NULL
);

CREATE TABLE analysis_results (
    id UUID PRIMARY KEY,
    request_id UUID NOT NULL UNIQUE REFERENCES analysis_requests(id),
    risk_level VARCHAR(50) NOT NULL,
    summary TEXT NOT NULL,
    ai_confidence_score DOUBLE PRECISION,
    findings_summary_json TEXT
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    actor_id UUID,
    action_type VARCHAR(255) NOT NULL,
    target_type VARCHAR(255),
    target_id UUID,
    metadata_json TEXT,
    created_at TIMESTAMP NOT NULL
);

-- Indexes for performance and tenant isolation
CREATE INDEX idx_users_tenant_id ON users(tenant_id);
CREATE INDEX idx_projects_tenant_id ON projects(tenant_id);
CREATE INDEX idx_analysis_requests_project_id ON analysis_requests(project_id);
CREATE INDEX idx_findings_request_id ON findings(request_id);
CREATE INDEX idx_audit_logs_actor_id ON audit_logs(actor_id);
