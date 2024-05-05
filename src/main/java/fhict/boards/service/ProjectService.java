package fhict.boards.service;

import fhict.boards.domain.dto.*;
import fhict.boards.repository.entity.Project;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProjectService {
    ProjectResponse createProject(ProjectCreateRequest request, MultipartFile icon) throws IOException;
    List<MemberResponse> inviteUsers(Long id, InviteUsersRequest request);
    List<ProjectResponse> getProjectsByUserId(Long userId);
    ProjectResponse getProjectById(Long id);
    byte[] updateProjectIcon(Long id, MultipartFile file) throws IOException;
    void deleteProjectIcon(Long id);
    ProjectResponse updateProjectDetails(Long id, ProjectUpdateRequest request);
    void deleteProject(Long id);
    Project getProjectByIdOrThrowNotFound(Long id);
}