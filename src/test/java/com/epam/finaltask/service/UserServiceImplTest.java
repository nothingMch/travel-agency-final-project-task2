package com.epam.finaltask.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import com.epam.finaltask.dto.RegistrationRequestDTO;
import com.epam.finaltask.exception.custom.EntityNotFoundException;
import com.epam.finaltask.exception.custom.UserRegistrationException;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private UserServiceImpl userService;

  @Test
  void getUserByUsername_UserExists_Success() {
    String username = "existingUser";
    User user = new User();
    user.setUsername(username);

    UserDTO expectedUserDTO = new UserDTO();
    expectedUserDTO.setUsername(username);

    when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
    when(userMapper.toUserDTO(any(User.class))).thenReturn(expectedUserDTO);

    UserDTO result = userService.getUserByUsername(username);

    assertNotNull(result, "The returned UserDTO should not be null");
    assertEquals(expectedUserDTO.getUsername(), result.getUsername(),
        "The username should match the expected value");

    verify(userRepository, times(1)).findUserByUsername(username);
    verify(userMapper, times(1)).toUserDTO(any(User.class));
  }

  @Test
  void changeAccountStatus_UserExist_Success() {
    String userId = UUID.randomUUID().toString();
    UserDTO userDTO = new UserDTO();
    userDTO.setId(userId);
    userDTO.setActive(true);

    User user = new User();
    user.setId(UUID.fromString(userId));
    user.setActive(false);

    User updatedUser = new User();
    updatedUser.setId(UUID.fromString(userId));
    updatedUser.setActive(true);

    when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));
    when(userMapper.toUser(any(UserDTO.class))).thenReturn(updatedUser);
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);
    when(userMapper.toUserDTO(any(User.class))).thenReturn(userDTO);

    UserDTO resultDTO = userService.changeAccountStatus(userDTO);

    assertNotNull(resultDTO, "The returned UserDTO should not be null");
    assertTrue(resultDTO.isActive(), "The account status should be updated to true");

    verify(userRepository, times(1)).findById(UUID.fromString(userId));
    verify(userRepository, times(1)).save(any(User.class));
  }


  @Test
  void getUserById_UserExist_Success() {
    UUID id = UUID.randomUUID();
    User user = new User();
    user.setId(id);

    UserDTO expectedUserDTO = new UserDTO();
    expectedUserDTO.setId(id.toString());

    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    when(userMapper.toUserDTO(any(User.class))).thenReturn(expectedUserDTO);

    UserDTO resultDTO = userService.getUserById(id);

    assertNotNull(resultDTO, "The returned UserDTO should not be null");
    assertEquals(expectedUserDTO.getId(), resultDTO.getId(),
        "The user ID should match the expected value");

    verify(userRepository, times(1)).findById(id);
    verify(userMapper, times(1)).toUserDTO(any(User.class));
  }

  //my tests

  @Test
  void userRegistration_Success() {
    RegistrationRequestDTO registrationDTO = new RegistrationRequestDTO();
    registrationDTO.setUsername("testUser");
    registrationDTO.setPassword("testPass");
    registrationDTO.setRepeatedPassword("testPass");
    registrationDTO.setEmail("test@example.com");
    registrationDTO.setPhoneNumber("097452363");

    String encodedPassword = "encodedPassword";
    User user = new User();
    user.setUsername(registrationDTO.getUsername());
    user.setPassword(encodedPassword);
    user.setEmail(registrationDTO.getEmail());
    user.setPhoneNumber(registrationDTO.getPhoneNumber());
    user.setRole(Role.USER);
    user.setBalance(BigDecimal.ZERO);
    user.setActive(true);

    User savedUser = copyOfUser(user);
    savedUser.setId(UUID.randomUUID());

    UserDTO expectedUserDTO = fromUserToDTO(savedUser);

    when(userRepository.existsByUsername(registrationDTO.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(false);
    when(passwordEncoder.encode(registrationDTO.getPassword())).thenReturn(encodedPassword);
    when(userMapper.toUserFromRegistration(registrationDTO, encodedPassword)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(savedUser);
    when(userMapper.toUserDTO(savedUser)).thenReturn(expectedUserDTO);

    UserDTO resultUserDTO = userService.register(registrationDTO);

    assertNotNull(resultUserDTO, "The returned UserDTO should not be null");
    assertEquals(expectedUserDTO, resultUserDTO,
            "The returned user object should match the expected user");

    verify(passwordEncoder, times(1)).encode(any(String.class));
    verify(userRepository, times(1)).save(any(User.class));
    verify(userRepository, times(1)).existsByUsername(registrationDTO.getUsername());
    verify(userRepository, times(1)).existsByEmail(registrationDTO.getEmail());
    verify(userMapper, times(1)).toUserDTO(savedUser);
    verify(userMapper, times(1)).toUserFromRegistration(registrationDTO, encodedPassword);
  }

  public static User copyOfUser(User user) {
    User copy = new User();
    copy.setId(user.getId());
    copy.setUsername(user.getUsername());
    copy.setPassword(user.getPassword());
    copy.setPhoneNumber(user.getPhoneNumber());
    copy.setEmail(user.getEmail());
    copy.setBalance(user.getBalance());
    copy.setRole(user.getRole());
    copy.setActive(user.isActive());
    if(user.getVouchers() != null){
      copy.setVouchers(new ArrayList<>(user.getVouchers()));
    }
    return copy;
  }

  public static UserDTO fromUserToDTO(User user) {
    UserDTO userDTO = new UserDTO();
    userDTO.setId(user.getId().toString());
    userDTO.setUsername(user.getUsername());
    userDTO.setPhoneNumber(user.getPhoneNumber());
    userDTO.setEmail(user.getEmail());
    userDTO.setBalance(user.getBalance().doubleValue());
    userDTO.setRole(user.getRole().toString());
    userDTO.setActive(user.isActive());

    return userDTO;
  }

  @Test
  void register_UserAlreadyExists_ThrowsException() {
    RegistrationRequestDTO registrationDTO = new RegistrationRequestDTO();
    registrationDTO.setEmail("test@example.com");
    registrationDTO.setUsername("testuser");

    when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(true);

    assertThrows(UserRegistrationException.class, () -> userService.register(registrationDTO));
    verify(userRepository, never()).save(any());
  }

  @Test
  void register_Success() {
    RegistrationRequestDTO registrationDTO = new RegistrationRequestDTO();
    registrationDTO.setEmail("test@example.com");
    registrationDTO.setUsername("testuser");
    registrationDTO.setPassword("password");
    registrationDTO.setRepeatedPassword("password");

    User user = new User();
    when(userRepository.existsByEmail(registrationDTO.getEmail())).thenReturn(false);
    when(userRepository.existsByUsername(registrationDTO.getUsername())).thenReturn(false);
    when(passwordEncoder.encode(registrationDTO.getPassword())).thenReturn("encodedPassword");
    when(userMapper.toUserFromRegistration(any(), any())).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toUserDTO(user)).thenReturn(new UserDTO());

    UserDTO result = userService.register(registrationDTO);
    assertNotNull(result);
  }

  @Test
  void getUserByUsername_UserNotFound_ThrowsException() {
    when(userRepository.findUserByUsername("unknown")).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class, () -> userService.getUserByUsername("unknown"));
  }

  @Test
  void getUserByUsername_Success() {
    User user = new User();
    user.setUsername("testuser");
    UserDTO userDTO = new UserDTO();

    when(userRepository.findUserByUsername("testuser")).thenReturn(Optional.of(user));
    when(userMapper.toUserDTO(user)).thenReturn(userDTO);

    UserDTO result = userService.getUserByUsername("testuser");
    assertNotNull(result);
  }

  @Test
  void deleteUser_UserExists_SuccessfulDeletion() {
    UUID userUUID = UUID.randomUUID();
    User mockUser = new User();
    mockUser.setId(userUUID);

    when(userRepository.findById(userUUID)).thenReturn(Optional.of(mockUser));
    doNothing().when(userRepository).delete(mockUser);
    when(userRepository.existsById(userUUID)).thenReturn(false);

    boolean result = userService.deleteUser(userUUID.toString());

    assertTrue(result, "The user should be successfully deleted");
    verify(userRepository).findById(userUUID);
    verify(userRepository).delete(mockUser);
    verify(userRepository).existsById(userUUID);
  }

  @Test
  void deleteUser_UserDoesNotExist_ThrowsException() {
    UUID userUUID = UUID.randomUUID();

    when(userRepository.findById(userUUID)).thenReturn(Optional.empty());

    EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
            () -> userService.deleteUser(userUUID.toString()));

    assertEquals("User not found", exception.getMessage());
    verify(userRepository).findById(userUUID);
    verify(userRepository, never()).delete(any(User.class));
    verify(userRepository, never()).existsById(any());
  }

  @Test
  void changeUserRole_Success() {
    UUID userId = UUID.randomUUID();
    Role newRole = Role.ADMIN;

    User user = new User();
    user.setRole(Role.USER);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toUserDTO(user)).thenReturn(new UserDTO());

    UserDTO result = userService.changeUserRole(userId.toString(), newRole);
    assertNotNull(result);
    assertEquals(newRole, user.getRole());
  }

}
