package fhict.boards.service;

import fhict.boards.domain.dto.EmailUpdateRequest;
import fhict.boards.domain.dto.PasswordUpdateRequest;
import fhict.boards.domain.dto.UserResponse;
import fhict.boards.domain.dto.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    void deleteUser(Long id);
    UserResponse updateUserDetails(Long id, UserUpdateRequest request);
    String updateUserEmail(Long id, EmailUpdateRequest request);
    void updateUserPassword(Long id, PasswordUpdateRequest request);
    byte[] updateUserPicture(Long id, MultipartFile file) throws IOException;
    void deleteUserPicture(Long id);
}
