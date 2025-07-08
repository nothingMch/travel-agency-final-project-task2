package com.epam.finaltask.controller.mvc;

import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.custom.EntityNotFoundException;
import com.epam.finaltask.service.TourService;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/tour")
public class TourController {

    private final TourService tourService;

    private final VoucherService voucherService;

    private final UserService userService;

    @Autowired
    public TourController(TourService tourService, VoucherService voucherService, UserService userService) {
        this.tourService = tourService;
        this.voucherService = voucherService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String viewTour(@PathVariable String id, Model model) {
        TourDTO tour = tourService.findById(id);
        model.addAttribute("tour", tour);
        return "tour/tour-details";
    }

    @PostMapping("/{id}/purchase")
    @PreAuthorize("hasRole('USER')")
    public String purchaseTour(@PathVariable UUID id, Model model) {
        VoucherDTO voucherDTO = new VoucherDTO();
        voucherDTO.setTourId(id.toString());
        voucherDTO.setUserId(userService.getCurrentAuthenticatedUserId().toString());

        voucherService.order(voucherDTO);

        model.addAttribute("message", "Your tour has been successfully purchased!");
        return "voucher/success";
    }

    @GetMapping("/my-tours")
    @PreAuthorize("hasRole('USER')")
    public String getUserTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        UUID userId = userService.getCurrentAuthenticatedUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("status")));
        Page<UserVoucherDTO> voucherPage = voucherService.getVouchersByUserId(userId, pageable);

        model.addAttribute("vouchers", voucherPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", voucherPage.getTotalPages());

        return "voucher/user-tours";
    }

    @PostMapping("/my-tours/cancel/{voucherId}")
    @PreAuthorize("@securityService.isOwner(#voucherId, authentication)")
    public String cancelVoucher(@PathVariable UUID voucherId, RedirectAttributes redirectAttributes) {
        try {
            voucherService.cancelVoucher(voucherId);
            redirectAttributes.addFlashAttribute("message", "Order canceled successfully!");
        } catch (EntityNotFoundException e) {
            System.out.println("erore EntityNotFoundException");
            redirectAttributes.addFlashAttribute("errorMessage", "Order not found!");
        } catch (Exception e) {
            System.out.println("erore Exception");
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to cancel order!");
        }
        return "redirect:/tour/my-tours";
    }

    @PostMapping("/{id}/make-hot")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String makeTourHot(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        tourService.changeHotStatus(id, true);
        redirectAttributes.addFlashAttribute("message", "Tour marked as HOT!");
        return "redirect:/";
    }

    @PostMapping("/{id}/remove-hot")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public String removeTourHot(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        tourService.changeHotStatus(id, false);
        redirectAttributes.addFlashAttribute("message", "Tour is no longer HOT!");
        return "redirect:/";
    }
}