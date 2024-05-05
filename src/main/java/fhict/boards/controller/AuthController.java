package fhict.boards.controller;

import fhict.boards.domain.dto.LoginRequest;
import fhict.boards.domain.dto.AccessTokenResponse;
import fhict.boards.domain.dto.SignupRequest;
import fhict.boards.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AccessTokenResponse> signup(@RequestBody @Valid SignupRequest request) {
        AccessTokenResponse accessToken = authService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(accessToken);
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@RequestBody @Valid LoginRequest request) {
        AccessTokenResponse accessToken = authService.loginUser(request);

        return ResponseEntity.ok(accessToken);
    }
}
