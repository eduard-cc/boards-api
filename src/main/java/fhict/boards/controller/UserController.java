package fhict.boards.controller;

import fhict.boards.domain.dto.*;
import fhict.boards.service.UserService;
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
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam("email") @Valid String email) {
        UserResponse user = userService.getUserByEmail(email);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}")
    public ResponseEntity<UserResponse> updateUserDetails(@PathVariable Long id,
                                                          @RequestBody @Valid UserUpdateRequest request) {
        UserResponse user = userService.updateUserDetails(id, request);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("{id}/email")
    public ResponseEntity<AccessTokenResponse> updateUserEmail(@PathVariable Long id,
                                                               @RequestBody @Valid EmailUpdateRequest request) {
        String accessToken = userService.updateUserEmail(id, request);

        return ResponseEntity.ok(new AccessTokenResponse(accessToken));
    }

    @PatchMapping("{id}/password")
    public ResponseEntity<Void> updateUserPassword(@PathVariable Long id,
                                                   @RequestBody @Valid PasswordUpdateRequest request) {
        userService.updateUserPassword(id, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/picture")
    public ResponseEntity<byte[]> updateUserPicture(@PathVariable Long id,
                                                    @RequestParam("image") MultipartFile file) throws IOException {
        byte[] picture = userService.updateUserPicture(id, file);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("image/png"))
                .body(picture);
    }

    @DeleteMapping("{id}/picture")
    public ResponseEntity<Void> deleteUserPicture(@PathVariable Long id) {
        userService.deleteUserPicture(id);

        return ResponseEntity.noContent().build();
    }
}
