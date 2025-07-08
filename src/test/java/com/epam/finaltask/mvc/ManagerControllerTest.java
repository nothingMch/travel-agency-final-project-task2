package com.epam.finaltask.mvc;

import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.service.VoucherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
public class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoucherService voucherService;


    @MockBean
    private JavaMailSender javaMailSender;

    private UUID testVoucherId;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testVoucherId = UUID.randomUUID();
        testUserId = UUID.randomUUID();
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void getAllVouchers_withoutStatus_shouldReturnVoucherList() throws Exception {
        UserVoucherDTO voucher = new UserVoucherDTO();
        Page<UserVoucherDTO> page = new PageImpl<>(List.of(voucher));

        when(voucherService.getAllVouchers(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/manager/vouchers")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/voucher-list"))
                .andExpect(model().attributeExists("vouchers", "currentPage", "totalPages"));

        verify(voucherService).getAllVouchers(any(PageRequest.class));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void getAllVouchers_withStatus_shouldReturnFilteredVoucherList() throws Exception {
        UserVoucherDTO voucher = new UserVoucherDTO();
        Page<UserVoucherDTO> page = new PageImpl<>(List.of(voucher));
        VoucherStatus status = VoucherStatus.REGISTERED;

        when(voucherService.getVouchersByStatus(eq(status), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/manager/vouchers")
                        .param("status", status.name())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/voucher-list"))
                .andExpect(model().attributeExists("vouchers", "currentPage", "totalPages", "status"))
                .andExpect(model().attribute("status", status));

        verify(voucherService).getVouchersByStatus(eq(status), any(PageRequest.class));
    }

    @Test
    void getAllVouchers_withoutAuthentication_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/manager/vouchers"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void updateVoucherStatus_whenSuccess_shouldRedirectWithMessage() throws Exception {
        doNothing().when(voucherService).updateVoucherStatus(eq(testVoucherId), eq(VoucherStatus.CANCELED));

        mockMvc.perform(post("/manager/vouchers/" + testVoucherId + "/update-status")
                        .with(csrf())
                        .param("status", "CANCELED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/vouchers"));

        verify(voucherService).updateVoucherStatus(eq(testVoucherId), eq(VoucherStatus.CANCELED));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void updateVoucherStatus_whenException_shouldRedirectWithErrorMessage() throws Exception {
        doThrow(new RuntimeException("Update failed")).when(voucherService).updateVoucherStatus(eq(testVoucherId), any());

        mockMvc.perform(post("/manager/vouchers/" + testVoucherId + "/update-status")
                        .with(csrf())
                        .param("status", "CANCELED"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/vouchers"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void searchVouchers_byVoucherId_whenFound_shouldReturnVoucherListWithOneItem() throws Exception {
        UserVoucherDTO voucher = new UserVoucherDTO();

        when(voucherService.getVoucherById(eq(testVoucherId))).thenReturn(voucher);

        mockMvc.perform(get("/manager/vouchers/search")
                        .param("voucherId", testVoucherId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/voucher-list"))
                .andExpect(model().attributeExists("vouchers"))
                .andExpect(model().attribute("vouchers", List.of(voucher)));

        verify(voucherService).getVoucherById(eq(testVoucherId));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void searchVouchers_byVoucherId_whenNotFound_shouldShowErrorMessage() throws Exception {
        when(voucherService.getVoucherById(eq(testVoucherId))).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/manager/vouchers/search")
                        .param("voucherId", testVoucherId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/voucher-list"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void searchVouchers_byUserId_whenFound_shouldReturnVoucherList() throws Exception {
        UserVoucherDTO voucher = new UserVoucherDTO();
        Page<UserVoucherDTO> page = new PageImpl<>(List.of(voucher));

        when(voucherService.getVouchersByUserId(eq(testUserId), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/manager/vouchers/search")
                        .param("userId", testUserId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/voucher-list"))
                .andExpect(model().attributeExists("vouchers"));

        verify(voucherService).getVouchersByUserId(eq(testUserId), any(PageRequest.class));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void searchVouchers_byUserId_whenException_shouldShowErrorMessage() throws Exception {
        when(voucherService.getVouchersByUserId(eq(testUserId), any(PageRequest.class))).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(get("/manager/vouchers/search")
                        .param("userId", testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("manager/voucher-list"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(roles = {"MANAGER"})
    void searchVouchers_withoutParams_shouldRedirectToAllVouchers() throws Exception {
        mockMvc.perform(get("/manager/vouchers/search"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/manager/vouchers"));
    }
}
