package com.epam.finaltask.service.impl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import com.epam.finaltask.repository.Specification.UserSpecifications;
import org.springframework.data.jpa.domain.Specification;
import com.epam.finaltask.dto.ChangePasswordDTO;
import com.epam.finaltask.dto.RegistrationRequestDTO;
import com.epam.finaltask.dto.UpdateUserDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.custom.CurrentUserNotFoundException;
import com.epam.finaltask.exception.custom.EntityNotFoundException;
import com.epam.finaltask.exception.custom.UpdateUserException;
import com.epam.finaltask.exception.custom.UserRegistrationException;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public UserDTO register(RegistrationRequestDTO registrationDTO) {
		logger.info("Attempting to register user with email: {} and username: {}", registrationDTO.getEmail(), registrationDTO.getUsername());

		if (userRepository.existsByEmail(registrationDTO.getEmail())) {
			throw new UserRegistrationException("Email already in use");
		}
		if (userRepository.existsByUsername(registrationDTO.getUsername())) {
			throw new UserRegistrationException("Username already in use");
		}
		if (!registrationDTO.getPassword().equals(registrationDTO.getRepeatedPassword())) {
			throw new UserRegistrationException("Passwords do not match");
		}

		String encodedPassword = passwordEncoder.encode(registrationDTO.getPassword());
		User user = userMapper.toUserFromRegistration(registrationDTO, encodedPassword);
		User savedUser = userRepository.save(user);
		UserDTO result = userMapper.toUserDTO(savedUser);

		logger.info("User registered successfully: {}", result);
		return result;
	}

	@Override
	@Transactional
	public UserDTO updateUser(String currentUsername, UpdateUserDTO updateUserDTO) {
		logger.info("Updating user with current username: {}", currentUsername);

		User user = userRepository.findUserByUsername(currentUsername)
				.orElseThrow(() -> {
					logger.error("Update failed: User {} not found", currentUsername);
					return new UpdateUserException("User not found");
				});

		if (!currentUsername.equals(updateUserDTO.getUsername()) &&
				userRepository.existsByUsername(updateUserDTO.getUsername())) {
			logger.error("Update failed: Username {} already in use", updateUserDTO.getUsername());
			throw new UpdateUserException("Username already in use");
		}

		logger.info("Updating user details for {}", currentUsername);
		user.setUsername(updateUserDTO.getUsername());
		user.setEmail(updateUserDTO.getEmail());
		user.setPhoneNumber(updateUserDTO.getPhoneNumber());

		User savedUser = userRepository.save(user);
		logger.info("User updated successfully: {}", savedUser.getUsername());

		if (!currentUsername.equals(updateUserDTO.getUsername())) {
			var authentication = SecurityContextHolder.getContext().getAuthentication();
			var newAuth = new UsernamePasswordAuthenticationToken(
					updateUserDTO.getUsername(),
					authentication.getCredentials(),
					authentication.getAuthorities()
			);
			SecurityContextHolder.getContext().setAuthentication(newAuth);
			logger.info("Security context updated for new username: {}", updateUserDTO.getUsername());
		}

		return userMapper.toUserDTO(savedUser);
	}

	@Override
	@Transactional
	public void changePassword(String username, ChangePasswordDTO dto) {
		logger.info("Changing password for user: {}", username);

		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> {
					logger.error("Change password failed: User {} not found", username);
					return new EntityNotFoundException("User not found");
				});

		if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
			logger.error("Change password failed: Incorrect old password for user {}", username);
			throw new IllegalArgumentException("Old password is incorrect");
		}

		String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
		user.setPassword(encodedNewPassword);
		userRepository.save(user);

		logger.info("Password changed successfully for user: {}", username);
	}

	@Override
	@Transactional
	public boolean deleteUser(String userId) {
		logger.info("Deleting user with ID: {}", userId);

		UUID userUUID = UUID.fromString(userId);
		User user = userRepository.findById(userUUID)
				.orElseThrow(() -> {
					logger.error("Delete failed: User {} not found", userId);
					return new EntityNotFoundException("User not found");
				});

		userRepository.delete(user);
		boolean exists = userRepository.existsById(userUUID);

		if (!exists) {
			logger.info("User deleted successfully: {}", userId);
		} else {
			logger.error("Delete failed: User {} still exists in the database", userId);
		}

		return !exists;
	}

	@Override
	@Transactional
	public void deleteUserByUsername(String username) {
		logger.info("Deleting user with username: {}", username);

		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> {
					logger.error("Delete failed: User {} not found", username);
					return new EntityNotFoundException("User not found");
				});

		userRepository.delete(user);
		logger.info("User with username {} deleted successfully", username);
	}

	@Override
	@Transactional
	public void addBalance(String username, Double amount) {
		logger.info("Adding balance for user: {}, Amount: {}", username, amount);

		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> {
					logger.error("Add balance failed: User {} not found", username);
					return new EntityNotFoundException("User not found");
				});

		user.setBalance(user.getBalance().add(BigDecimal.valueOf(amount)));
		userRepository.save(user);

		logger.info("Balance added successfully for user: {}, New Balance: {}", username, user.getBalance());
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		logger.info("Fetching user details by username: {}", username);

		UserDTO result = userRepository.findUserByUsername(username)
				.map(userMapper::toUserDTO)
				.orElseThrow(() -> {
					logger.error("User not found with username: {}", username);
					return new EntityNotFoundException("User not found with username: " + username);
				});

		logger.info("User details fetched successfully: {}", result);
		return result;
	}

	@Override
	public UserDTO getUserById(UUID id) {
		logger.info("Fetching user details by ID: {}", id);

		UserDTO result = userMapper.toUserDTO(findUserEntityById(id));

		logger.info("User details fetched successfully for ID {}: {}", id, result);
		return result;
	}

	@Override
	@Transactional
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		logger.info("Changing account status for user: {}", userDTO);

		User user = userMapper.toUser(userDTO);
		user = findUserEntityById(user.getId());
		user.setActive(userDTO.isActive());
		UserDTO result = userMapper.toUserDTO(userRepository.save(user));

		logger.info("Account status changed successfully for user: {}", result);
		return result;
	}

	@Override
	@Transactional
	public UserDTO changeUserRole(String userId, Role role) {
		logger.info("Changing role for user ID: {}, New Role: {}", userId, role);

		User user = userRepository.findById(UUID.fromString(userId))
				.orElseThrow(() -> {
					logger.error("Change role failed: User {} not found", userId);
					return new EntityNotFoundException("User not found");
				});

		user.setRole(role);
		UserDTO result = userMapper.toUserDTO(userRepository.save(user));

		logger.info("Role changed successfully for user ID {}: {}", userId, result);
		return result;
	}

	@Override
	public Page<UserDTO> getAllUsers(Pageable pageable) {
		logger.info("Fetching all users with pagination: {}", pageable);

		Page<UserDTO> result = userRepository.findAll(pageable)
				.map(userMapper::toUserDTO);

		logger.info("Fetched users: {}", result);
		return result;
	}

	@Override
	public String getCurrentAuthenticatedUsername() {
		logger.debug("Fetching current authenticated username");

		var authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getPrincipal().equals("anonymousUser")) {
			logger.debug("Failed to fetch authenticated username: User is not authenticated");
			throw new CurrentUserNotFoundException("User is not authenticated");
		}

		Object principal = authentication.getPrincipal();
		String username;

		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}

		logger.debug("Current authenticated username: {}", username);
		return username;
	}

	@Override
	public UUID getCurrentAuthenticatedUserId() {
		logger.info("Fetching current authenticated user ID");

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			UUID userId = userRepository.findUserByUsername(username)
					.map(User::getId)
					.orElseThrow(() -> {
						logger.error("User ID not found for authenticated user: {}", username);
						return new EntityNotFoundException("User not found");
					});

			logger.info("Current authenticated user ID: {}", userId);
			return userId;
		} else {
			logger.error("Failed to fetch authenticated user ID: Principal is not a UserDetails instance");
			throw new IllegalStateException("Authentication principal is not a UserDetails instance");
		}
	}

	@Override
	public Page<UserDTO> findUsersFiltered(String id, String username, String email, String role, Boolean active, Pageable pageable) {
		logger.info("Finding users with filters - ID: {}, Username: {}, Email: {}, Role: {}, Active: {}", id, username, email, role, active);

		Specification<User> spec = Specification.where(null);

		Specification<User> idSpec = UserSpecifications.idEquals(id);
		if (idSpec != null) spec = spec.and(idSpec);

		Specification<User> usernameSpec = UserSpecifications.usernameContains(username);
		if (usernameSpec != null) spec = spec.and(usernameSpec);

		Specification<User> emailSpec = UserSpecifications.emailContains(email);
		if (emailSpec != null) spec = spec.and(emailSpec);

		Specification<User> roleSpec = UserSpecifications.hasRole(role);
		if (roleSpec != null) spec = spec.and(roleSpec);

		Specification<User> activeSpec = UserSpecifications.isActive(active);
		if (activeSpec != null) spec = spec.and(activeSpec);

		Page<UserDTO> result = userRepository.findAll(spec, pageable).map(userMapper::toUserDTO);

		logger.info("Filtered users fetched: {}", result);
		return result;
	}

	@Override
	@Transactional
	public void toggleActive(String userId) {
		logger.info("Toggling active status for user ID: {}", userId);

		User user = userRepository.findById(UUID.fromString(userId))
				.orElseThrow(() -> {
					logger.error("Toggle active status failed: User {} not found", userId);
					return new EntityNotFoundException("User not found");
				});

		user.setActive(!user.isActive());
		userRepository.save(user);

		logger.info("Active status toggled successfully for user ID: {}, New Status: {}", userId, user.isActive());
	}

    @Override
    public Optional<User> findByEmail(String email) {
		logger.info("Fetching user details by email: {}", email);
        return userRepository.findByEmail(email);
    }

	@Override
	public void updatePassword(User user, String password) {
		logger.info("Updating password for user with email: {}", user.getEmail());
		user.setPassword(passwordEncoder.encode(password));
		userRepository.save(user);
	}

	private User findUserEntityById(UUID id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
	}

}
