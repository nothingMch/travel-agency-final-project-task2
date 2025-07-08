package com.epam.finaltask.controller.rest;

import com.epam.finaltask.dto.PageResponseDTO;
import com.epam.finaltask.dto.UpdateUserDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, UserDTO>> getUserByUsername(@PathVariable String username) {
        UserDTO userDTO = userService.getUserByUsername(username);
        System.out.println("asdlasdlasldlsaldlasldlas " + userDTO);
        Map<String, UserDTO> response = Map.of("result", userDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<Map<String, UserDTO>> getUserById(@PathVariable UUID userId) {
        UserDTO userDTO = userService.getUserById(userId);
        Map<String, UserDTO> response = Map.of("result", userDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{username}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable String username, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        userService.updateUser(username, updateUserDTO);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", "User successfully updated"
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        boolean isDeleted = userService.deleteUser(userId);
        Map<String, String> response = Map.of(
                "statusCode", isDeleted ? "OK" : "FAILED",
                "statusMessage", isDeleted ? "User successfully deleted" : "Failed to delete user"
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/status")
    public ResponseEntity<Map<String, String>> changeAccountStatus(@Valid @RequestBody UserDTO userDTO) {
        userService.changeAccountStatus(userDTO);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", "User account status successfully updated"
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/role/{userId}")
    public ResponseEntity<Map<String, String>> changeUserRole(@PathVariable String userId, @RequestParam Role role) {
        userService.changeUserRole(userId, role);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", String.format("User role successfully changed to %s", role)
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, PageResponseDTO<UserDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDTO> usersPage = userService.getAllUsers(pageable);

        PageResponseDTO<UserDTO> response = new PageResponseDTO<>();
        response.setContent(usersPage.getContent());
        response.setPageNumber(usersPage.getNumber());
        response.setPageSize(usersPage.getSize());
        response.setTotalElements(usersPage.getTotalElements());

        return ResponseEntity.ok(Map.of("results", response));
    }
}
