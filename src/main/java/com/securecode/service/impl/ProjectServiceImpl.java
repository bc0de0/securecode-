package com.securecode.service.impl;

import com.securecode.dto.ProjectDTO;
import com.securecode.model.Project;
import com.securecode.model.Tenant;
import com.securecode.repository.ProjectRepository;
import com.securecode.repository.TenantRepository;
import com.securecode.service.AuditService;
import com.securecode.service.ProjectService;
import com.securecode.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TenantRepository tenantRepository;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        return projectRepository.findAllByTenantId(tenantId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectDTO dto) {
        UUID tenantId = SecurityUtils.getCurrentTenantId();
        UUID userId = SecurityUtils.getCurrentUserId();

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalStateException("Tenant context missing"));

        Project project = Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .tenant(tenant)
                .createdBy(userId)
                .build();

        project = projectRepository.save(project);

        auditService.log(userId, "PROJECT_CREATION", "Project", project.getId(), Map.of("name", project.getName()));

        return mapToDTO(project);
    }

    private ProjectDTO mapToDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .contextDocs(project.getContextDocs())
                .aiUserContext(project.getAiUserContext())
                .tenantId(project.getTenant().getId())
                .createdBy(project.getCreatedBy())
                .createdAt(project.getCreatedAt())
                .build();
    }
}
