package com.epam.finaltask.controller.mvc;

import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.TourService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/tour")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTourController {

    private final TourService tourService;

    @Autowired
    public AdminTourController(TourService tourService) {
        this.tourService = tourService;
    }

    @GetMapping("/update/{id}")
    public String getUpdateTourPage(@PathVariable String id, Model model) {
        TourDTO tour = tourService.findById(id);
        model.addAttribute("tour", tour);

        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());

        return "admin/update-tour";
    }

    @PostMapping("/update/{id}")
    public String updateTour(
            @PathVariable String id,
            @Valid @ModelAttribute("tour") TourDTO tourDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("tour", tourDTO);
            return "admin/update-tour";
        }

        tourService.update(id, tourDTO);
        model.addAttribute("message", "Tour updated successfully!");
        return "redirect:/";
    }

    @PostMapping("/{id}/delete")
    public String deleteTour(@PathVariable String id, Model model) {
        tourService.delete(id);
        model.addAttribute("message", "Tour deleted successfully!");
        return "redirect:/";
    }

    @GetMapping("/add")
    public String getAddTourPage(Model model) {
        model.addAttribute("tour", new TourDTO());

        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());

        return "admin/add-tour";
    }

    @PostMapping("/add")
    public String addTour(
            @Valid @ModelAttribute("tour") TourDTO tourDTO,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("tourTypes", TourType.values());
            model.addAttribute("transferTypes", TransferType.values());
            model.addAttribute("hotelTypes", HotelType.values());
            return "admin/add-tour";
        }

        tourService.create(tourDTO);

        model.addAttribute("message", "Tour added successfully!");
        return "redirect:/";
    }
}