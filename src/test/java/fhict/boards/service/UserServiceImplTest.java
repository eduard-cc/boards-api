package fhict.boards.service;

import fhict.boards.domain.dto.EmailUpdateRequest;
import fhict.boards.domain.dto.PasswordUpdateRequest;
import fhict.boards.domain.dto.UserResponse;
import fhict.boards.domain.dto.UserUpdateRequest;
import fhict.boards.domain.enums.AccessRole;
import fhict.boards.exception.EmailAlreadyExistsException;
import fhict.boards.exception.InvalidCredentialsException;
import fhict.boards.exception.UnauthorizedAccessException;
import fhict.boards.exception.UserNotFoundException;
import fhict.boards.repository.IssueRepository;
import fhict.boards.repository.MemberRepository;
import fhict.boards.repository.NotificationRepository;
import fhict.boards.repository.UserRepository;
import fhict.boards.repository.entity.User;
import fhict.boards.security.token.AccessToken;
import fhict.boards.security.token.AccessTokenSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccessToken requestAccessToken;
    @Mock
    private AccessTokenSerializer accessTokenSerializer;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private DtoMapper dtoMapper;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private IssueRepository issueRepository;
    @Mock
    private MemberRepository memberRepository;

    @Test
    void getAllUsers_WhenUsersExist_ShouldReturnListOfUsers() {
        User user1 = User.builder()
                .id(1L)
                .name("User1")
                .email("user1@gmail.com")
                .build();
        User user2 = User.builder()
                .id(2L)
                .name("User2")
                .email("user2@gmail.com")
                .build();

        List<User> userEntities = Arrays.asList(user1, user2);
        List<UserResponse> users = userEntities.stream()
                .map(dtoMapper::mapToUserResponse)
                .toList();

        when(userRepository.findAll()).thenReturn(userEntities);

        for (int i = 0; i < users.size(); i++) {
            when(dtoMapper.mapToUserResponse(userEntities.get(i))).thenReturn(users.get(i));
        }

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(users, result);
    }

    @Test
    void getAllUsers_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserResponse> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .name("User1")
                .email("user1@gmail.com")
                .build();
        UserResponse expectedUser = UserResponse.builder()
                .id(userId)
                .name("User1")
                .email("user1@gmail.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dtoMapper.mapToUserResponse(user)).thenReturn(expectedUser);

        UserResponse actualUser = userService.getUserById(userId);

        verify(userRepository).findById(userId);
        verify(dtoMapper).mapToUserResponse(user);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getUserById_WhenIdIsNull_ShouldThrowException() {
        Long nullUserId = null;

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(nullUserId));
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUserResponse() {
        String userEmail = "user@example.com";
        User user = User.builder()
                .id(1L)
                .name("User1")
                .email(userEmail)
                .build();
        UserResponse expectedUser = UserResponse.builder()
                .id(1L)
                .name("User1")
                .email(userEmail)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(dtoMapper.mapToUserResponse(any())).thenReturn(expectedUser);

        UserResponse actualUser = userService.getUserByEmail(userEmail);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserByEmail_WhenUserDoesNotExist_ShouldThrowException() {
        String userEmail = "nonexistent@example.com";
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(userEmail));
    }

    @Test
    void getUserByEmail_WhenEmailIsNull_ShouldThrowException() {
        String nullUserEmail = null;

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(nullUserEmail));
    }

    @Test
    void deleteUser_WhenUserExistsAndIsAuthorized_ShouldDeleteUser() {
        Long userId = 1L;

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(requestAccessToken.getRole()).thenReturn(AccessRole.USER);

        doNothing().when(notificationRepository).deleteByUserId(userId);
        doNothing().when(issueRepository).setAssigneeAndCreatedByToNull(userId);
        doNothing().when(memberRepository).deleteAllByUserId(userId);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository).deleteById(userId);
        verify(notificationRepository).deleteByUserId(userId);
        verify(issueRepository).setAssigneeAndCreatedByToNull(userId);
        verify(memberRepository).deleteAllByUserId(userId);
    }

    @Test
    void deleteUser_WhenUnauthorized_ShouldThrowException() {
        Long authenticatedUserId = 1L;
        Long userIdToDelete = 2L;
        Mockito.when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);
        when(requestAccessToken.getRole()).thenReturn(AccessRole.USER);

        assertThrows(UnauthorizedAccessException.class, () -> userService.deleteUser(userIdToDelete));

        verify(userRepository, Mockito.never()).deleteById(userIdToDelete);
    }

    @Test
    void updateUserDetails_WhenUserExistsAndIsAuthorized_ShouldUpdateAndReturnUser() {
        Long userId = 1L;
        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("New Name")
                .company("New Company")
                .location("New Location")
                .build();

        User existingUser = new User();
        existingUser.setId(userId);

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(requestAccessToken.getRole()).thenReturn(AccessRole.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(dtoMapper.mapToUserResponse(existingUser)).thenReturn(new UserResponse());

        UserResponse updatedUser = userService.updateUserDetails(userId, request);

        verify(userRepository).save(existingUser);
        assertEquals(request.getName(), existingUser.getName());
        assertEquals(request.getCompany(), existingUser.getCompany());
        assertEquals(request.getLocation(), existingUser.getLocation());
        assertNotNull(updatedUser);
    }

    @Test
    void updateUserDetails_WhenUnauthorizedAccess_ShouldThrowException() {
        Long userId = 1L;
        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("New Name")
                .company("New Company")
                .location("New Location")
                .build();

        when(requestAccessToken.getUserId()).thenReturn(2L);
        when(requestAccessToken.getRole()).thenReturn(AccessRole.USER);

        assertThrows(UnauthorizedAccessException.class, () -> userService.updateUserDetails(userId, request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserDetails_WhenUserDoesNotExist_ShouldThrowException() {
        Long userId = 1L;
        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("New Name")
                .company("New Company")
                .location("New Location")
                .build();

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(requestAccessToken.getRole()).thenReturn(AccessRole.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserDetails(userId, request));
        verify(userRepository, never()).save(any(User.class));
    }


    @Test
    void updateUserDetails_WithNullRequest_ShouldNotUpdateAndReturnUser() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(requestAccessToken.getRole()).thenReturn(AccessRole.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(dtoMapper.mapToUserResponse(existingUser)).thenReturn(new UserResponse());

        UserResponse updatedUser = userService.updateUserDetails(userId, null);

        verify(userRepository, never()).save(existingUser);
        assertNotNull(updatedUser);
    }

    @Test
    void updateUserEmail_WhenAuthorizedAndEmailChangeValid_ShouldUpdateEmailAndReturnToken() {
        Long userId = 1L;
        String currentEmail = "user@example.com";
        String newEmail = "newemail@example.com";
        EmailUpdateRequest request = new EmailUpdateRequest(newEmail);

        when(requestAccessToken.getUserId()).thenReturn(userId);

        User user = User.builder()
                .id(userId)
                .email(currentEmail)
                .accessRole(AccessRole.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(accessTokenSerializer.encode(ArgumentMatchers.any(AccessToken.class))).thenReturn("encoded_token");

        String result = userService.updateUserEmail(userId, request);

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(newEmail);
        verify(userRepository).save(user);

        assertEquals("encoded_token", result);
        assertEquals(newEmail, user.getEmail());
    }

    @Test
    void updateUserEmail_WhenUnauthorized_ShouldThrowException() {
        Long authenticatedUserId = 1L;
        Long userIdToChange = 2L;
        String newEmail = "newemail@example.com";
        EmailUpdateRequest request = new EmailUpdateRequest(newEmail);

        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        assertThrows(UnauthorizedAccessException.class, () -> userService.updateUserEmail(userIdToChange, request));

        verify(userRepository, Mockito.never()).findById(userIdToChange);
        verify(userRepository, Mockito.never()).findByEmail(newEmail);
        verify(userRepository, Mockito.never()).save(any(User.class));
        verify(accessTokenSerializer, Mockito.never()).encode(any(AccessToken.class));
    }

    @Test
    void updateUserEmail_WhenNewEmailAlreadyExists_ShouldThrowException() {
        Long userId = 1L;
        String currentEmail = "user@example.com";
        String newEmail = "newemail@example.com";
        EmailUpdateRequest request = new EmailUpdateRequest(newEmail);

        when(requestAccessToken.getUserId()).thenReturn(userId);

        User user = new User();
        user.setId(userId);
        user.setEmail(currentEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUserEmail(userId, request));

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(newEmail);
        verify(userRepository, Mockito.never()).save(any(User.class));
        verify(accessTokenSerializer, Mockito.never()).encode(any(AccessToken.class));
    }

    @Test
    void updateUserEmail_WhenNewEmailAlreadyExists_ShouldThrowEmailAlreadyExistsException() {
        // Arrange
        Long userId = 1L;
        String currentEmail = "user@example.com";
        String newEmail = "newemail@example.com";
        EmailUpdateRequest request = new EmailUpdateRequest(newEmail);

        when(requestAccessToken.getUserId()).thenReturn(userId);

        User user = User.builder()
                .id(userId)
                .email(currentEmail)
                .accessRole(AccessRole.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUserEmail(userId, request));

        // Verify interactions
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(newEmail);
        verify(userRepository, never()).save(any(User.class));
        verify(accessTokenSerializer, never()).encode(any(AccessToken.class));
    }

    @Test
    void updateUserPassword_WhenAuthorizedAndPasswordChangeValid_ShouldUpdatePassword() {
        Long userId = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        PasswordUpdateRequest request = new PasswordUpdateRequest(currentPassword, newPassword);

        when(requestAccessToken.getUserId()).thenReturn(userId);

        String encodedCurrentPassword = passwordEncoder.encode(currentPassword);

        User user = User.builder()
                .id(userId)
                .password(encodedCurrentPassword)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);

        userService.updateUserPassword(userId, request);

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);

        String updatedPassword = user.getPassword();
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        assertEquals(encodedNewPassword, updatedPassword);
    }

    @Test
    void updateUserPassword_WhenUnauthorized_ShouldThrowException() {
        Long authenticatedUserId = 1L;
        Long userIdToChange = 2L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        PasswordUpdateRequest request = new PasswordUpdateRequest(currentPassword, newPassword);

        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        assertThrows(UnauthorizedAccessException.class, () -> userService.updateUserPassword(userIdToChange, request));

        verify(userRepository, Mockito.never()).findById(userIdToChange);
        verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void updateUserPassword_WhenUserNotFound_ShouldThrowException() {
        Long userId = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        PasswordUpdateRequest request = new PasswordUpdateRequest(currentPassword, newPassword);

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserPassword(userId, request));

        verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void updateUserPassword_WhenInvalidCurrentPassword_ShouldThrowException() {
        Long userId = 1L;
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        PasswordUpdateRequest request = new PasswordUpdateRequest(currentPassword, newPassword);

        when(requestAccessToken.getUserId()).thenReturn(userId);

        User user = User.builder()
                .id(userId)
                .password(passwordEncoder.encode("differentPassword"))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.updateUserPassword(userId, request));

        verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void updateUserPicture_WhenAuthorized_ShouldUpdatePicture() throws IOException {
        Long userId = 1L;
        byte[] newPicture = "New Picture Data".getBytes();
        MultipartFile file = Mockito.mock(MultipartFile.class);

        when(requestAccessToken.getUserId()).thenReturn(userId);

        User user = User.builder()
                .id(userId)
                .picture("Old Picture Data".getBytes())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(file.getBytes()).thenReturn(newPicture);

        byte[] result = userService.updateUserPicture(userId, file);

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);

        assertArrayEquals(newPicture, result);
    }

    @Test
    void updateUserPicture_WhenUnauthorized_ShouldThrowException() {
        Long authenticatedUserId = 1L;
        Long userIdToUpdate = 2L;
        MultipartFile file = Mockito.mock(MultipartFile.class);

        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        assertThrows(UnauthorizedAccessException.class, () -> userService.updateUserPicture(userIdToUpdate, file));

        verify(userRepository, Mockito.never()).findById(userIdToUpdate);
        verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void updateUserPicture_WhenUserNotFound_ShouldThrowException() {
        Long userId = 1L;
        MultipartFile file = Mockito.mock(MultipartFile.class);

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserPicture(userId, file));

        verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void deleteUserPicture_WhenAuthorized_ShouldDeletePicture() {
        Long userId = 1L;

        when(requestAccessToken.getUserId()).thenReturn(userId);

        User user = User.builder()
                .id(userId)
                .picture("Picture Data".getBytes())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserPicture(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);

        assertNull(user.getPicture());
    }

    @Test
    void deleteUserPicture_WhenUnauthorized_ShouldThrowException() {
        Long authenticatedUserId = 1L;
        Long userIdToDelete = 2L;

        when(requestAccessToken.getUserId()).thenReturn(authenticatedUserId);

        assertThrows(UnauthorizedAccessException.class, () -> userService.deleteUserPicture(userIdToDelete));

        verify(userRepository, Mockito.never()).findById(userIdToDelete);
        verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void deleteUserPicture_WhenUserNotFound_ShouldThrowException() {
        Long userId = 1L;

        when(requestAccessToken.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserPicture(userId));

        verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }
}