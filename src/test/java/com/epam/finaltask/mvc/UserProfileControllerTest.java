package com.epam.finaltask.mvc;

import com.epam.finaltask.dto.UpdateUserDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.jwt.JwtUtil;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.config.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = com.epam.finaltask.Application.class,
        properties = {
                "jwt.secret=12345678901234567890123456789012",
                "jwt.expiration=900000",
                "jwt.refresh-expiration=604800000"
        }
)
@AutoConfigureMockMvc
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JavaMailSender javaMailSender;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;


    private final String username = "testUser";

    private UserDetails testUserDetails;

    private String userJwt;

    @BeforeEach
    void setup() {
        when(userService.getCurrentAuthenticatedUsername()).thenReturn(username);

        UserDetails fakeUserDetails = org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password("password")
                .roles("USER")
                .build();

        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(fakeUserDetails);
        when(jwtUtil.createToken(fakeUserDetails)).thenReturn("fake-jwt-token");

        userJwt = "fake-jwt-token";
    }

    @Test
    void getUserProfile_shouldReturnProfileView() throws Exception {
        UserDTO userDTO = new UserDTO();
        Mockito.lenient().when(userService.getUserByUsername(username)).thenReturn(userDTO);

        mockMvc.perform(get("/profile").with(user(username).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("user/user-profile"))
                .andExpect(model().attributeExists("UpdateUserForm"));

        verify(userService, atLeastOnce()).getUserByUsername(username);
    }

    @Test
    void updateUserProfile_whenValid_shouldRedirectProfileWithMessage() throws Exception {
        mockMvc.perform(post("/profile/update")
                        .with(user(username).roles("USER"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", username)
                        .param("email", "email@example.com")
                        .param("phoneNumber", "1234567890")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute("message", "Profile updated successfully!"));

        verify(userService).updateUser(eq(username), any(UpdateUserDTO.class));
    }


    @Test
    void updateUserProfile_whenValidationErrors_shouldReturnForm() throws Exception {
        mockMvc.perform(post("/profile/update")
                                .with(user(username).roles("USER"))
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user/user-profile"))
                .andExpect(model().attributeExists("UpdateUserForm"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void updateUserProfile_whenUsernameChanged_shouldLogoutAndRedirectToLogin() throws Exception {
        String oldUsername = "testUser";
        String newUsername = "newUsername";

        when(userService.getCurrentAuthenticatedUsername()).thenReturn(oldUsername);
        when(userService.updateUser(Mockito.eq(oldUsername), any(UpdateUserDTO.class))).thenReturn(new UserDTO());

        try (MockedStatic<JwtUtil> jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.logoutAndClearTokens(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                    .thenAnswer(invocation -> null);

            mockMvc.perform(post("/profile/update")
                            .param("username", newUsername)
                            .param("email", "newemail@example.com")
                            .param("phoneNumber", "1234567890")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/auth/login"))
                    .andExpect(flash().attribute("message", "Username changed. Please log in again."));

            jwtUtilMock.verify(() -> JwtUtil.logoutAndClearTokens(any(HttpServletRequest.class), any(HttpServletResponse.class)), times(1));
        }
    }


    @Test
    void deleteUserAccount_shouldLogoutAndRedirectToLogin() throws Exception {
        doNothing().when(userService).deleteUserByUsername(anyString());

        mockMvc.perform(post("/profile/delete")
                        .cookie(new Cookie("jwt", userJwt))
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?logout"))
                .andExpect(flash().attributeExists("message"));

        verify(userService).deleteUserByUsername(anyString());
    }

    @Test
    void addBalance_shouldRedirectWithMessage() throws Exception {
        doNothing().when(userService).addBalance(eq(username), anyDouble());

        mockMvc.perform(post("/profile/add-balance")
                        .with(user(username).roles("USER"))
                        .with(csrf())
                        .param("amount", "100.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute("message", "Balance added successfully!"));

        verify(userService).addBalance(eq(username), eq(100.0));
    }

    @Test
    void showChangePasswordForm_shouldReturnView() throws Exception {
        mockMvc.perform(get("/profile/change-password").with(user(username).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("user/change-password"))
                .andExpect(model().attributeExists("changePasswordDTO"));
    }

    @Test
    void changePassword_whenValid_shouldRedirectWithMessage() throws Exception {
        doNothing().when(userService).changePassword(eq(username), any());

        mockMvc.perform(post("/profile/change-password")
                        .with(user(username).roles("USER"))
                        .with(csrf())
                        .param("oldPassword", "oldPass")
                        .param("newPassword", "NewPass123!")
                        .param("confirmNewPassword", "NewPass123!")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute("message", "Password changed successfully!"));

        verify(userService).changePassword(eq(username), any());
    }

    @Test
    void changePassword_whenValidationErrors_shouldReturnForm() throws Exception {
        mockMvc.perform(post("/profile/change-password")
                                .with(user(username).roles("USER"))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user/change-password"));
    }

    @Test
    void changePassword_whenPasswordsDoNotMatch_shouldReturnFormWithError() throws Exception {
        mockMvc.perform(post("/profile/change-password")
                        .with(user(username).roles("USER"))
                        .with(csrf())
                        .param("oldPassword", "oldPass")
                        .param("newPassword", "NewPass123!")
                        .param("confirmNewPassword", "Mismatch123!")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user/change-password"))
                .andExpect(model().attributeHasFieldErrors("changePasswordDTO", "confirmNewPassword"));
    }

    @Test
    void changePassword_whenServiceThrowsIllegalArgumentException_shouldReturnFormWithError() throws Exception {
        doThrow(new IllegalArgumentException("Incorrect old password")).when(userService).changePassword(eq(username), any());

        mockMvc.perform(post("/profile/change-password")
                        .with(user(username).roles("USER"))
                        .with(csrf())
                        .param("oldPassword", "wrongOldPass")
                        .param("newPassword", "NewPass123!")
                        .param("confirmNewPassword", "NewPass123!")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user/change-password"))
                .andExpect(model().attributeHasFieldErrors("changePasswordDTO", "oldPassword"));
    }
}
