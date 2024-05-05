package fhict.boards.controller;

import fhict.boards.domain.dto.*;
import fhict.boards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @RolesAllowed("ADMIN")
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        UserResponse user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get a user by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @GetMapping("/email")
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "Email") @RequestParam("email") @Valid String email) {
        UserResponse user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{id}")
    public ResponseEntity<UserResponse> updateUserDetails(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "User update request") @RequestBody @Valid UserUpdateRequest request) {
        UserResponse user = userService.updateUserDetails(id, request);

        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User email updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{id}/email")
    public ResponseEntity<AccessTokenResponse> updateUserEmail(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Email update request") @RequestBody @Valid EmailUpdateRequest request) {
        String accessToken = userService.updateUserEmail(id, request);

        return ResponseEntity.ok(new AccessTokenResponse(accessToken));
    }

    @Operation(summary = "Update user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{id}/password")
    public ResponseEntity<Void> updateUserPassword(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Password update request") @RequestBody @Valid PasswordUpdateRequest request) {
        userService.updateUserPassword(id, request);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user picture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User picture updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PatchMapping("{id}/picture")
    public ResponseEntity<byte[]> updateUserPicture(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "Image file") @RequestParam("image") MultipartFile file) throws IOException {
        byte[] picture = userService.updateUserPicture(id, file);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/png"))
                .body(picture);
    }

    @Operation(summary = "Delete user picture")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User picture deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @DeleteMapping("{id}/picture")
    public ResponseEntity<Void> deleteUserPicture(
            @Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUserPicture(id);

        return ResponseEntity.noContent().build();
    }
}
