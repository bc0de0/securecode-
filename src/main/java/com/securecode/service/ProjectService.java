package com.securecode.service;

import com.securecode.dto.ProjectDTO;
import java.util.List;

public interface ProjectService {
    List<ProjectDTO> getAllProjects();

    ProjectDTO createProject(ProjectDTO dto);
}
