package fhict.boards.controller;

import fhict.boards.domain.dto.*;
import fhict.boards.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestPart("request") @Valid ProjectCreateRequest request,
                                                         @RequestPart(value = "icon", required = false)
                                                         MultipartFile icon) throws IOException {
        ProjectResponse createdProject = projectService.createProject(request, icon);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjectsByUserId(@RequestParam Long userId) {
        List<ProjectResponse> projects = projectService.getProjectsByUserId(userId);

        return ResponseEntity.ok(projects);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse project = projectService.getProjectById(id);

        return ResponseEntity.ok(project);
    }

    @PatchMapping("{id}")
    public ResponseEntity<ProjectResponse> updateProjectDetails(@PathVariable Long id,
                                                                @RequestBody @Valid ProjectUpdateRequest request) {
        ProjectResponse project = projectService.updateProjectDetails(id, request);

        return ResponseEntity.ok(project);
    }

    @PatchMapping("{id}/icon")
    public ResponseEntity<byte[]> updateProjectIcon(@PathVariable Long id,
                                                    @RequestParam("image") MultipartFile file) throws IOException {
        byte[] icon = projectService.updateProjectIcon(id, file);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/png"))
                .body(icon);
    }

    @DeleteMapping("{id}/icon")
    public ResponseEntity<Void> deleteProjectIcon(@PathVariable Long id) {
        projectService.deleteProjectIcon(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/members")
    public ResponseEntity<List<MemberResponse>> inviteMembers(@PathVariable Long id,
                                                              @RequestBody @Valid InviteUsersRequest request) {
        List<MemberResponse> members = projectService.inviteUsers(id, request);

        return ResponseEntity.ok(members);
    }
}
