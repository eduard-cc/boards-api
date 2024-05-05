package fhict.boards.service;

import fhict.boards.domain.dto.AccessTokenResponse;
import fhict.boards.domain.dto.LoginRequest;
import fhict.boards.domain.dto.SignupRequest;
import fhict.boards.exception.EmailAlreadyExistsException;
import fhict.boards.exception.InvalidCredentialsException;
import fhict.boards.exception.UserNotFoundException;
import fhict.boards.repository.UserRepository;
import fhict.boards.domain.enums.AccessRole;
import fhict.boards.repository.entity.User;
import fhict.boards.security.token.AccessToken;
import fhict.boards.security.token.AccessTokenSerializer;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenSerializer accessTokenSerializer;

    public AccessTokenResponse createUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .accessRole(AccessRole.USER)
                .build();

        userRepository.save(user);

        AccessToken accessToken = AccessToken.builder()
                .subject(user.getEmail())
                .userId(user.getId())
                .role(user.getAccessRole())
                .build();

        return new AccessTokenResponse(accessTokenSerializer.encode(accessToken));
    }

    public AccessTokenResponse loginUser(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(request.getEmail());
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        AccessToken accessToken = AccessToken.builder()
                .subject(user.getEmail())
                .userId(user.getId())
                .role(user.getAccessRole())
                .build();

        return new AccessTokenResponse(accessTokenSerializer.encode(accessToken));
    }
}
