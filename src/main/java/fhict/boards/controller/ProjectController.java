package fhict.boards.controller;

import fhict.boards.domain.dto.*;
import fhict.boards.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a new project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Parameter(description = "Project create request") @RequestPart("request") @Valid ProjectCreateRequest request,
            @Parameter(description = "Icon file") @RequestPart(value = "icon", required = false) MultipartFile icon) throws IOException {
        ProjectResponse createdProject = projectService.createProject(request, icon);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    @Operation(summary = "Get projects by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjectsByUserId(
            @Parameter(description = "User ID") @RequestParam Long userId) {
        List<ProjectResponse> projects = projectService.getProjectsByUserId(userId);

        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Update project details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project details updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{id}")
    public ResponseEntity<ProjectResponse> updateProjectDetails(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Parameter(description = "Project update request") @RequestBody @Valid ProjectUpdateRequest request) {
        ProjectResponse project = projectService.updateProjectDetails(id, request);

        return ResponseEntity.ok(project);
    }

    @Operation(summary = "Update project icon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project icon updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{id}/icon")
    public ResponseEntity<byte[]> updateProjectIcon(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Parameter(description = "Image file") @RequestParam("image") MultipartFile file) throws IOException {
        byte[] icon = projectService.updateProjectIcon(id, file);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/png"))
                .body(icon);
    }

    @Operation(summary = "Delete project icon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project icon deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("{id}/icon")
    public ResponseEntity<Void> deleteProjectIcon(
            @Parameter(description = "Project ID") @PathVariable Long id) {
        projectService.deleteProjectIcon(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Project ID") @PathVariable Long id) {
        projectService.deleteProject(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Invite members to a project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Members invited successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{id}/members")
    public ResponseEntity<List<MemberResponse>> inviteMembers(
            @Parameter(description = "Project ID") @PathVariable Long id,
            @Parameter(description = "Invite users request") @RequestBody @Valid InviteUsersRequest request) {
        List<MemberResponse> members = projectService.inviteUsers(id, request);

        return ResponseEntity.ok(members);
    }
}
