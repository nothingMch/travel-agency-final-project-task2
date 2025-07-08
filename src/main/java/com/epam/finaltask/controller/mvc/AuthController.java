package com.epam.finaltask.controller.mvc;

import com.epam.finaltask.dto.LoginRequestDTO;
import com.epam.finaltask.dto.RegistrationRequestDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.jwt.JwtUtil;
import com.epam.finaltask.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public String getLoginPage(Model model){
        model.addAttribute("loginRequest", new LoginRequestDTO());
        return "auth/sign-in";
    }

    @PostMapping("/login")
    @PreAuthorize("isAnonymous()")
    public String login(@Valid @ModelAttribute LoginRequestDTO loginRequestDTO,
                        BindingResult bindingResult,
                        Model model,
                        HttpServletResponse response,
                        RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginRequest", loginRequestDTO);
            return "auth/sign-in";
        }

        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDTO.getUsername(),
                            loginRequestDTO.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String jwt = jwtUtil.createToken(userDetails);
            String refreshJwt = jwtUtil.createRefreshToken(userDetails);

            Cookie accessTokenCookie = new Cookie("jwt", jwt);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(15 * 60);

            Cookie refreshTokenCookie = new Cookie("refreshJwt", refreshJwt);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            return "redirect:/";

        } catch (AuthenticationException ex) {
            bindingResult.reject("login.failed", "Invalid username or password");
            model.addAttribute("loginRequest", loginRequestDTO);
            return "auth/sign-in";
        }
    }

    @GetMapping("/registration")
    @PreAuthorize("isAnonymous()")
    public String getRegistrationPage(@ModelAttribute("errorMessage") String errorMessage, Model model) throws Exception {
        model.addAttribute("registrationRequest", new RegistrationRequestDTO());
        return "auth/sign-up";
    }

    @PostMapping("/registration")
    @PreAuthorize("isAnonymous()")
    public String userRegistration(@Valid @ModelAttribute("registrationRequest") RegistrationRequestDTO registrationDTO,
                                   BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes){

        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationRequest", registrationDTO);
            return "auth/sign-up";
        }

        userService.register(registrationDTO);
        redirectAttributes.addFlashAttribute("message",
                "Your account has been successfully registered. Please log in.");
        return "redirect:/auth/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        deleteCookie("jwt", response);
        deleteCookie("refreshJwt", response);

        request.getSession(false);
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }

        return "redirect:/";
    }

    private void deleteCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @GetMapping("/refresh")
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshJwt".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return "redirect:/auth/login?error=missing_refresh_token";
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtUtil.isValid(refreshToken, userDetails)) {
                return "redirect:/auth/login?error=invalid_refresh_token";
            }

            String newAccessToken = jwtUtil.createToken(userDetails);
            Cookie accessTokenCookie = new Cookie("jwt", newAccessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(15 * 60);
            response.addCookie(accessTokenCookie);

            return "redirect:/";

        } catch (io.jsonwebtoken.JwtException e) {
            return "redirect:/auth/login?error=expired_or_invalid_token";
        }
    }

}
