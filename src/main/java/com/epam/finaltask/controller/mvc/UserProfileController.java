package com.epam.finaltask.controller.mvc;

import com.epam.finaltask.dto.ChangePasswordDTO;
import com.epam.finaltask.dto.UpdateUserDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.custom.UpdateUserException;
import com.epam.finaltask.jwt.JwtUtil;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.impl.UserServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.epam.finaltask.jwt.JwtUtil.logoutAndClearTokens;

@Controller
@RequestMapping("/profile")
@PreAuthorize("isAuthenticated()")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public String getUserProfile(Model model) {
        String username = userService.getCurrentAuthenticatedUsername();
        UserDTO user = userService.getUserByUsername(username);
        model.addAttribute("UpdateUserForm", user);
        return "user/user-profile";
    }


    @PostMapping("/update")
    public String updateUserProfile(
            @Valid @ModelAttribute("UpdateUserForm") UpdateUserDTO updateUserDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        logger.info("Updating user was started");

        if (bindingResult.hasErrors()) {
            logger.info("Validation failed");
            model.addAttribute("UpdateUserForm", updateUserDTO);
            return "user/user-profile";
        }

        try {
            String currentUsername = userService.getCurrentAuthenticatedUsername();
            userService.updateUser(currentUsername, updateUserDTO);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");

            if(!currentUsername.equals(updateUserDTO.getUsername())){
                logger.info("Username changed: '{}' -> '{}'", currentUsername, updateUserDTO.getUsername());
                logoutAndClearTokens(request, response);
                redirectAttributes.addFlashAttribute("message", "Username changed. Please log in again.");
                return "redirect:/auth/login";
            }
        } catch (Exception ex) {
            logger.info("Failed to update profile: " + ex.getMessage());
            throw new UpdateUserException("Failed to update profile: " + ex.getMessage());
        }

        logger.info("Successful update");
        return "redirect:/profile";
    }

    @PostMapping("/delete")
    public String deleteUserAccount(RedirectAttributes redirectAttributes, HttpServletResponse response,
                                    HttpServletRequest request) {

        String username = userService.getCurrentAuthenticatedUsername();
        userService.deleteUserByUsername(username);

        logoutAndClearTokens(request, response);
        redirectAttributes.addFlashAttribute("message",
                "Your account has been successfully deleted. Please log in again.");
        return "redirect:/auth/login?logout";
    }

    @PostMapping("/add-balance")
    @PreAuthorize("hasRole('USER')")
    public String addBalance(@RequestParam Double amount, RedirectAttributes redirectAttributes) {
        String username = userService.getCurrentAuthenticatedUsername();
        userService.addBalance(username, amount);
        redirectAttributes.addFlashAttribute("message", "Balance added successfully!");
        return "redirect:/profile";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        return "user/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO changePasswordDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "user/change-password";
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmNewPassword())) {
            bindingResult.rejectValue("confirmNewPassword", "error.changePasswordDTO", "Passwords do not match");
            return "user/change-password";
        }

        String username = userService.getCurrentAuthenticatedUsername();

        try {
            userService.changePassword(username, changePasswordDTO);
            redirectAttributes.addFlashAttribute("message", "Password changed successfully!");
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("oldPassword", "error.changePasswordDTO", e.getMessage());
            return "user/change-password";
        }
    }
}
