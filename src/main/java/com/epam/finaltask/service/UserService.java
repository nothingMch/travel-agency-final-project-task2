package com.epam.finaltask.service;

import java.util.Optional;
import java.util.UUID;

import com.epam.finaltask.dto.ChangePasswordDTO;
import com.epam.finaltask.dto.RegistrationRequestDTO;
import com.epam.finaltask.dto.UpdateUserDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    UserDTO register(RegistrationRequestDTO registrationDTO);

    @Transactional
    UserDTO updateUser(String currentUsername, UpdateUserDTO updateUserDTO);

    @Transactional
    void changePassword(String username, ChangePasswordDTO passwordDTO);

    boolean deleteUser(String userId);

    @Transactional
    void deleteUserByUsername(String username);

    @Transactional
    void addBalance(String username, Double amount);

    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);

    UserDTO changeUserRole(String userId, Role role);

    Page<UserDTO> getAllUsers(Pageable pageable);

    String getCurrentAuthenticatedUsername();

    UUID getCurrentAuthenticatedUserId();

    Page<UserDTO> findUsersFiltered(String id,
                                    String username,
                                    String email,
                                    String role,
                                    Boolean active,
                                    Pageable pageable);

    public void toggleActive(String id);

    Optional<User> findByEmail(String email);

    void updatePassword(User user, String password);
}
