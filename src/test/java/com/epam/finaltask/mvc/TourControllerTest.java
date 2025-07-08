package com.epam.finaltask.mvc;

import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.exception.custom.EntityNotFoundException;
import com.epam.finaltask.jwt.JwtUtil;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.service.TourService;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import com.epam.finaltask.service.config.CustomUserDetailsService;
import com.epam.finaltask.service.config.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


@SpringBootTest(
        classes = com.epam.finaltask.Application.class,
        properties = {
                "jwt.secret=12345678901234567890123456789012",
                "jwt.expiration=900000",
                "jwt.refresh-expiration=604800000"
        }
)
@AutoConfigureMockMvc
public class TourControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private VoucherService voucherService;

    @MockBean
    private UserService userService;

    @MockBean
    private TourService tourService;

    @MockBean
    private SecurityService securityService;


    private UUID testUserId;
    private UserDetails testUserDetails;
    private String userJwt;

    private UUID testTourId;
    private UUID testVoucherId;

    private UserDetails managerDetails;
    private String managerJwt;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testTourId = UUID.randomUUID();
        testVoucherId = UUID.randomUUID();

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setActive(true);
        user.setRole(Role.USER);

        User manager = new User();
        manager.setUsername("managerUser");
        manager.setPassword("password");
        manager.setActive(true);
        manager.setRole(Role.MANAGER);

        when(userRepository.findUserByUsername("testUser")).thenReturn(Optional.of(user));
        when(userRepository.findUserByUsername("managerUser")).thenReturn(Optional.of(manager));

        when(userService.getCurrentAuthenticatedUserId()).thenReturn(testUserId);

        when(securityService.isOwner(eq(testVoucherId), any())).thenReturn(true);

        testUserDetails = customUserDetailsService.loadUserByUsername("testUser");
        userJwt = jwtUtil.createToken(testUserDetails);

        managerDetails = customUserDetailsService.loadUserByUsername("managerUser");
        managerJwt = jwtUtil.createToken(managerDetails);
    }

    @Test
    void viewTour_shouldReturnTourDetailsView() throws Exception {
        TourDTO tourDTO = new TourDTO();
        when(tourService.findById(testTourId.toString())).thenReturn(tourDTO);

        mockMvc.perform(get("/tour/" + testTourId)
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("tour/tour-details"))
                .andExpect(model().attributeExists("tour"));


        verify(tourService).findById(testTourId.toString());
    }

    @Test
    void purchaseTour_withUserRoleAndCsrf_shouldReturnSuccess() throws Exception {
        when(voucherService.order(any())).thenReturn(null);

        mockMvc.perform(post("/tour/" + testTourId + "/purchase")
                        .cookie(new Cookie("jwt", userJwt))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("voucher/success"))
                .andExpect(model().attribute("message", "Your tour has been successfully purchased!"));

        verify(voucherService).order(argThat(v ->
                v.getTourId().equals(testTourId.toString()) &&
                        v.getUserId().equals(testUserId.toString())
        ));
    }

    @Test
    void getUserTours_withUserRole_shouldReturnUserTours() throws Exception {
        UserVoucherDTO userVoucherDTO = new UserVoucherDTO();
        Page<UserVoucherDTO> page = new org.springframework.data.domain.PageImpl<>(List.of(userVoucherDTO));
        when(voucherService.getVouchersByUserId(eq(testUserId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/tour/my-tours")
                        .cookie(new Cookie("jwt", userJwt)))
                .andExpect(status().isOk())
                .andExpect(view().name("voucher/user-tours"))
                .andExpect(model().attributeExists("vouchers"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"));

        verify(voucherService).getVouchersByUserId(eq(testUserId), any(Pageable.class));
    }

    @Test
    void cancelVoucher_whenSuccess_shouldRedirectWithMessage() throws Exception {
        doNothing().when(voucherService).cancelVoucher(testVoucherId);

        mockMvc.perform(post("/tour/my-tours/cancel/" + testVoucherId)
                        .cookie(new Cookie("jwt", userJwt))
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tour/my-tours"))
                .andExpect(flash().attribute("message", "Order canceled successfully!"));

        verify(voucherService).cancelVoucher(testVoucherId);
    }

    @Test
    void cancelVoucher_whenEntityNotFound_shouldRedirectWithError() throws Exception {
        doThrow(new EntityNotFoundException("not found")).when(voucherService).cancelVoucher(testVoucherId);

        mockMvc.perform(post("/tour/my-tours/cancel/" + testVoucherId)
                        .cookie(new Cookie("jwt", userJwt))
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tour/my-tours"))
                .andExpect(flash().attribute("errorMessage", "Order not found!"));
    }

    @Test
    void cancelVoucher_whenOtherException_shouldRedirectWithError() throws Exception {
        doThrow(new RuntimeException("error")).when(voucherService).cancelVoucher(testVoucherId);

        mockMvc.perform(post("/tour/my-tours/cancel/" + testVoucherId)
                        .cookie(new Cookie("jwt", userJwt))
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tour/my-tours"))
                .andExpect(flash().attribute("errorMessage", "Failed to cancel order!"));
    }

    @Test
    void makeTourHot_withManagerRole_shouldRedirectWithMessage() throws Exception {
        doNothing().when(tourService).changeHotStatus(testTourId, true);

        mockMvc.perform(post("/tour/" + testTourId + "/make-hot")
                        .cookie(new Cookie("jwt", managerJwt))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Tour marked as HOT!"));

        verify(tourService).changeHotStatus(testTourId, true);
    }

    @Test
    void removeTourHot_withManagerRole_shouldRedirectWithMessage() throws Exception {
        doNothing().when(tourService).changeHotStatus(testTourId, false);

        mockMvc.perform(post("/tour/" + testTourId + "/remove-hot")
                        .cookie(new Cookie("jwt", managerJwt))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("message", "Tour is no longer HOT!"));

        verify(tourService).changeHotStatus(testTourId, false);
    }
}
