package com.epam.finaltask.controller.mvc;

import com.epam.finaltask.dto.FilterToursRequestDTO;
import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.service.TourService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private TourService tourService;

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String search,
            @Valid FilterToursRequestDTO filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "isHot"));
        Page<TourDTO> toursPage = tourService.getFilteredTours(search, filter, pageable);

        model.addAttribute("tours", toursPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", toursPage.getTotalPages());
        model.addAttribute("search", search);
        model.addAttribute("filter", filter);

        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());

        return "index";
    }

}
