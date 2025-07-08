package com.epam.finaltask.controller.mvc;

import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/manager/vouchers")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class ManagerController {

    private final VoucherService voucherService;

    @Autowired
    public ManagerController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping
    public String getAllVouchers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) VoucherStatus status,
            Model model) {

        Page<UserVoucherDTO> vouchersPage;
        if (status != null) {
            vouchersPage = voucherService.getVouchersByStatus(status, PageRequest.of(page, size));
        } else {
            vouchersPage = voucherService.getAllVouchers(PageRequest.of(page, size));
        }

        model.addAttribute("vouchers", vouchersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vouchersPage.getTotalPages());
        model.addAttribute("status", status);

        return "manager/voucher-list";
    }

    @PostMapping("/{id}/update-status")
    public String updateVoucherStatus(
            @PathVariable UUID id,
            @RequestParam VoucherStatus status,
            Model model) {

        try {
            voucherService.updateVoucherStatus(id, status);
            model.addAttribute("message", "Voucher status updated to " + status + " successfully!");
        } catch (Exception ex) {
            model.addAttribute("errorMessage", "Failed to update voucher status: " + ex.getMessage());
        }

        return "redirect:/manager/vouchers";
    }

    @GetMapping("/search")
    public String searchVouchers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UUID voucherId,
            @RequestParam(required = false) UUID userId,
            Model model) {

        if (voucherId != null) {
            try {
                UserVoucherDTO voucher = voucherService.getVoucherById(voucherId);
                model.addAttribute("vouchers", List.of(voucher));
            } catch (Exception ex) {
                model.addAttribute("errorMessage", "Voucher not found: " + ex.getMessage());
            }
        } else if (userId != null) {
            try {
                Page<UserVoucherDTO> vouchers = voucherService.getVouchersByUserId(userId, PageRequest.of(page, size));
                model.addAttribute("vouchers", vouchers.getContent());
            } catch (Exception ex) {
                model.addAttribute("errorMessage", "User vouchers not found: " + ex.getMessage());
            }
        } else {
            return "redirect:/manager/vouchers";
        }

        return "manager/voucher-list";
    }

}
