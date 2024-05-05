package fhict.boards.service;

import fhict.boards.domain.dto.AccessTokenResponse;
import fhict.boards.domain.dto.LoginRequest;
import fhict.boards.domain.dto.SignupRequest;

public interface AuthService {
    AccessTokenResponse createUser(SignupRequest request);
    AccessTokenResponse loginUser(LoginRequest request);
}
