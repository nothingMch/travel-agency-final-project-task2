package com.epam.finaltask.controller.mvc;

import com.epam.finaltask.dto.PasswordDto;
import com.epam.finaltask.model.PasswordResetToken;
import com.epam.finaltask.model.User;
import com.epam.finaltask.service.PasswordResetService;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.config.EmailService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@PreAuthorize("isAnonymous()")
public class PasswordResetController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    private final EmailService emailService;

    public PasswordResetController(UserService userService,
                                   PasswordResetService passwordResetService,
                                   EmailService emailService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "password-reset/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> optionalUser = userService.findByEmail(email);
        if (optionalUser.isPresent()) {
            PasswordResetToken token = passwordResetService.createToken(optionalUser.get());
            boolean emailSent = emailService.sendResetLink(optionalUser.get().getEmail(), token.getToken());
            if (!emailSent) {
                model.addAttribute("error", "Failed to send reset email. Please try again later.");
                return "password-reset/forgot-password";
            }
        }
        model.addAttribute("message", "If an account with that email exists, a reset link has been sent.");
        return "password-reset/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token,
                                        Model model,
                                        @ModelAttribute("passwordDto") PasswordDto passwordDto) {
        if (!model.containsAttribute("passwordDto")) {
            model.addAttribute("passwordDto", new PasswordDto());
        }
        if (passwordResetService.validateToken(token).isEmpty()) {
            model.addAttribute("error", "Invalid or expired token");
        }
        model.addAttribute("token", token);
        return "password-reset/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam("token") String token,
            @Valid @ModelAttribute("passwordDto") PasswordDto passwordDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.passwordDto", bindingResult);
            redirectAttributes.addFlashAttribute("passwordDto", passwordDto);
            redirectAttributes.addAttribute("token", token); // чтобы token был в URL
            return "redirect:/reset-password";
        }

        Optional<User> optionalUser = passwordResetService.validateToken(token);
        if (optionalUser.isPresent()) {
            userService.updatePassword(optionalUser.get(), passwordDto.getPass());
            passwordResetService.deleteToken(token);

            redirectAttributes.addFlashAttribute("message", "Password successfully reset!");
            return "redirect:/auth/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired token");
            redirectAttributes.addAttribute("token", token);
            return "redirect:/reset-password";
        }
    }
}