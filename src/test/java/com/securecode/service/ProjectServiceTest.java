package com.securecode.service;

import com.securecode.dto.ProjectDTO;
import com.securecode.model.Project;
import com.securecode.model.Tenant;
import com.securecode.repository.ProjectRepository;
import com.securecode.repository.TenantRepository;
import com.securecode.service.impl.ProjectServiceImpl;
import com.securecode.util.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TenantRepository tenantRepository;
    @Mock
    private AuditService auditService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private UUID tenantId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        userId = UUID.randomUUID();
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(SecurityUtils::getCurrentTenantId).thenReturn(tenantId);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void testGetAllProjects_Success() {
        Tenant tenant = Tenant.builder().id(tenantId).build();
        Project project = Project.builder()
                .id(UUID.randomUUID())
                .name("SecureProjectA")
                .tenant(tenant)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        when(projectRepository.findAllByTenantId(tenantId)).thenReturn(Collections.singletonList(project));

        List<ProjectDTO> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SecureProjectA", result.get(0).getName());
        verify(projectRepository).findAllByTenantId(tenantId);
    }

    @Test
    void testCreateProject_Success() {
        String projectName = "NewAnalysisProject";
        ProjectDTO dto = ProjectDTO.builder().name(projectName).build();

        Tenant tenant = Tenant.builder().id(tenantId).build();
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        Project savedProject = Project.builder()
                .id(UUID.randomUUID())
                .name(projectName)
                .tenant(tenant)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        ProjectDTO result = projectService.createProject(dto);

        assertNotNull(result);
        assertEquals(projectName, result.getName());
        verify(projectRepository).save(any(Project.class));
        verify(auditService).log(eq(userId), eq("PROJECT_CREATION"), eq("Project"), any(), any());
    }
}
