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
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;
    private final AccessToken requestAccessToken;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenSerializer accessTokenSerializer;
    private final DtoMapper dtoMapper;
    private final NotificationRepository notificationRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(dtoMapper::mapToUserResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = getUserByIdOrThrowNotFound(id);

        return dtoMapper.mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UserNotFoundException(email);
        }

        return dtoMapper.mapToUserResponse(user.get());
    }

    @Override
    public void deleteUser(Long id) {
        if (requestAccessToken.getRole() == AccessRole.USER && !Objects.equals(requestAccessToken.getUserId(), id)) {
            throw new UnauthorizedAccessException("Authenticated user is unauthorized to delete this user.");
        }

        notificationRepository.deleteByUserId(id);
        issueRepository.setAssigneeAndCreatedByToNull(id);
        memberRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse updateUserDetails(Long id, UserUpdateRequest request) {
        if (requestAccessToken.getRole() == AccessRole.USER && !Objects.equals(requestAccessToken.getUserId(), id)) {
            throw new UnauthorizedAccessException("Authenticated user is unauthorized to update this user's details.");
        }

        User user = getUserByIdOrThrowNotFound(id);

        if (request != null) {
            user.setName(request.getName());
            user.setCompany(request.getCompany());
            user.setLocation(request.getLocation());

            userRepository.save(user);
        }

        return dtoMapper.mapToUserResponse(user);
    }

    @Override
    public String updateUserEmail(Long id, EmailUpdateRequest request) {
        if (!Objects.equals(requestAccessToken.getUserId(), id)) {
            throw new UnauthorizedAccessException("Authenticated user is unauthorized to change this user's email.");
        }

        User user = getUserByIdOrThrowNotFound(id);

        if (Objects.equals(user.getEmail(), request.getNewEmail())) {
            throw new EmailAlreadyExistsException("User's email is already " + request.getNewEmail());
        }

        if (userRepository.findByEmail(request.getNewEmail()).isPresent()) {
            throw new EmailAlreadyExistsException();
        }

        user.setEmail(request.getNewEmail());

        userRepository.save(user);

        AccessToken accessToken = AccessToken.builder()
                .subject(user.getEmail())
                .userId(user.getId())
                .role(user.getAccessRole())
                .build();

        return accessTokenSerializer.encode(accessToken);
    }

    @Override
    public void updateUserPassword(Long id, PasswordUpdateRequest request) {
        if (!Objects.equals(requestAccessToken.getUserId(), id)) {
            throw new UnauthorizedAccessException("Authenticated user is unauthorized to change this user's password.");
        }

        User user = getUserByIdOrThrowNotFound(id);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Incorrect current password.");
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    @Override
    public byte[] updateUserPicture(Long id, MultipartFile file) throws IOException {
        if (!Objects.equals(requestAccessToken.getUserId(), id)) {
            throw new UnauthorizedAccessException("Authenticated user is unauthorized to update this user's picture.");
        }

        User user = getUserByIdOrThrowNotFound(id);

        user.setPicture(file.getBytes());
        userRepository.save(user);

        return user.getPicture();
    }

    @Override
    public void deleteUserPicture(Long id) {
        if (!Objects.equals(requestAccessToken.getUserId(), id)) {
            throw new UnauthorizedAccessException("Authenticated user is unauthorized to delete this user's picture.");
        }

        User user = getUserByIdOrThrowNotFound(id);
        user.setPicture(null);

        userRepository.save(user);
    }

    public User getUserByIdOrThrowNotFound(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        return user.get();
    }
}
