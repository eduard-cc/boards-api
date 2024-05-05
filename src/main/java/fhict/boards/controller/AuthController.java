package fhict.boards.controller;

import fhict.boards.domain.dto.LoginRequest;
import fhict.boards.domain.dto.AccessTokenResponse;
import fhict.boards.domain.dto.SignupRequest;
import fhict.boards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Sign up a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/signup")
    public ResponseEntity<AccessTokenResponse> signup(@Parameter(description = "Signup request")
                                                      @RequestBody @Valid SignupRequest request) {
        AccessTokenResponse accessToken = authService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(accessToken);
    }

    @Operation(summary = "Login a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Parameter(description = "Login request")
                                                     @RequestBody @Valid LoginRequest request) {
        AccessTokenResponse accessToken = authService.loginUser(request);

        return ResponseEntity.ok(accessToken);
    }
}
