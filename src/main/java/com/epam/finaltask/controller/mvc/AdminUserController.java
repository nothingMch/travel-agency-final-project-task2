package com.epam.finaltask.controller.mvc;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());

        Page<UserDTO> usersPage = userService.findUsersFiltered(id, username, email, role, active, pageable);

        model.addAttribute("usersPage", usersPage);
        model.addAttribute("filterId", id);
        model.addAttribute("filterUsername", username);
        model.addAttribute("filterEmail", email);
        model.addAttribute("filterRole", role);
        model.addAttribute("filterActive", active);
        model.addAttribute("roles", List.of("USER", "MANAGER", "ADMIN"));
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        return "admin/users-list";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleUserActive(@PathVariable String id) {
        userService.toggleActive(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/change-role")
    public String changeUserRole(@PathVariable String id, @Valid @RequestParam String role) {
        userService.changeUserRole(id, Role.valueOf(role));
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}