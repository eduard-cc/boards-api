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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AccessTokenSerializer accessTokenSerializer;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void createUser_WhenEmailDoesNotExist_ShouldCreateUserAndReturnToken() {
        SignupRequest request = SignupRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password("hashedPassword")
                .accessRole(AccessRole.USER)
                .build();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(accessTokenSerializer.encode(ArgumentMatchers.any(AccessToken.class))).thenReturn("encoded_token");

        AccessTokenResponse result = authService.createUser(request);

        verify(userRepository).existsByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
        verify(accessTokenSerializer).encode(ArgumentMatchers.any(AccessToken.class));

        assertEquals(new AccessTokenResponse("encoded_token"), result);
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowException() {
        SignupRequest request = SignupRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.createUser(request));

        verify(userRepository).existsByEmail(request.getEmail());
        verifyNoMoreInteractions(passwordEncoder, userRepository, accessTokenSerializer);
    }

    @Test
    void loginUser_WhenValidCredentials_ShouldReturnToken() {
        String userEmail = "john@example.com";
        String password = "password";
        LoginRequest request = new LoginRequest(userEmail, password);

        User user = User.builder()
                .id(1L)
                .email(userEmail)
                .password(passwordEncoder.encode(password))
                .accessRole(AccessRole.USER)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        AccessToken expectedAccessToken = AccessToken.builder()
                .subject(userEmail)
                .userId(user.getId())
                .role(user.getAccessRole())
                .build();

        when(accessTokenSerializer.encode(expectedAccessToken)).thenReturn("encoded_token");

        AccessTokenResponse result = authService.loginUser(request);

        verify(userRepository).findByEmail(userEmail);
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(accessTokenSerializer).encode(expectedAccessToken);

        assertEquals(new AccessTokenResponse("encoded_token"), result);
    }

    @Test
    void loginUser_WhenUserNotFound_ShouldThrowException() {
        String userEmail = "john@example.com";
        String password = "password";
        LoginRequest request = new LoginRequest(userEmail, password);

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.loginUser(request));

        verify(userRepository).findByEmail(userEmail);
        verifyNoInteractions(passwordEncoder, accessTokenSerializer);
    }

    @Test
    void loginUser_WhenInvalidCredentials_ShouldThrowException() {
        String userEmail = "john@example.com";
        String password = "password";
        LoginRequest request = new LoginRequest(userEmail, password);

        User user = User.builder()
                .id(1L)
                .email(userEmail)
                .password(passwordEncoder.encode("different_password"))
                .accessRole(AccessRole.USER)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(request));

        verify(userRepository).findByEmail(userEmail);
        verify(passwordEncoder).matches(password, user.getPassword());
        verifyNoInteractions(accessTokenSerializer);
    }
}