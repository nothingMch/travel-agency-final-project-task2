package com.epam.finaltask.controller.rest;

import com.epam.finaltask.dto.FilterToursRequestDTO;
import com.epam.finaltask.dto.PageResponseDTO;
import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.service.TourService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tours")
public class TourRestController {

    @Autowired
    private TourService tourService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Page<TourDTO>>> findAllByUserId(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String userId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TourDTO> tours = tourService.findAllByUserId(userId, pageable);
        Map<String, Page<TourDTO>> response = Map.of("results", tours);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{tourId}/status")
    public ResponseEntity<Map<String, String>> changeTourStatus(@PathVariable String tourId,
                                                @RequestBody boolean isHot){

        tourService.changeHotStatus(UUID.fromString(tourId), isHot);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", "Tour status is successfully changed"
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createTour(@Valid @RequestBody TourDTO tourDTO){
        tourService.create(tourDTO);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", "Tour is successfully created"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<Map<String, String>> deleteTour(@PathVariable String tourId){
        tourService.delete(tourId);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", String.format("Tour with Id %s has been deleted", tourId)
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("{tourId}")
    public ResponseEntity<Map<String, String>> updateTour(@PathVariable String tourId,
                              @Valid @RequestBody TourDTO tourDTO){

        tourService.update(tourId, tourDTO);
        Map<String, String> response = Map.of(
                "statusCode", "OK",
                "statusMessage", "Tour is successfully updated"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Page<TourDTO>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Pageable pageable = PageRequest.of(page, size);
        Page<TourDTO> tours = tourService.findAll(pageable);
        Map<String, Page<TourDTO>> response = Map.of("results", tours);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<Map<String, TourDTO>> findById(@PathVariable String tourId){
        TourDTO tourDTO = tourService.findById(tourId);
        Map<String, TourDTO> response = Map.of(
                "result", tourDTO
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Page<TourDTO>>> filterTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Valid @RequestBody FilterToursRequestDTO filter){

        Pageable pageable = PageRequest.of(page, size);
        Page<TourDTO> toursPage = tourService.filterTours(filter, pageable);

        Map<String, Page<TourDTO>> response = Map.of("results", toursPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Page<TourDTO>>> searchTours(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestBody String titlePart){

        Pageable pageable = PageRequest.of(page, size);
        Page<TourDTO> tours = tourService.searchToursByTitle(titlePart, pageable);
        Map<String, Page<TourDTO>> response = Map.of(
                "results", tours
        );
        return ResponseEntity.ok(response);
    }
}
