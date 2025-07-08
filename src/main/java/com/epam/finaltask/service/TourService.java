package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;

import com.epam.finaltask.dto.FilterToursRequestDTO;
import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TourService {

    TourDTO create(TourDTO tourDTO);
    TourDTO update(String id, TourDTO tourDTO);
    void delete(String tourId);

    void changeHotStatus(UUID tourId, boolean isHot);

    Page<TourDTO> findAllByUserId(String userId, Pageable pageable);
    Page<TourDTO> findAllByTourType(TourType tourType, Pageable pageable);
    Page<TourDTO> findAllByTransferType(TransferType transferType, Pageable pageable);
    Page<TourDTO> findAllByMinPrice(Double price, Pageable pageable);
    Page<TourDTO> findAllByHotelType(HotelType hotelType, Pageable pageable);
    Page<TourDTO> findAll(Pageable pageable);

    TourDTO findById(String id);

    Page<TourDTO> filterTours(FilterToursRequestDTO filter, Pageable pageable);

    Page<TourDTO> getFilteredTours(String search, FilterToursRequestDTO filter, Pageable pageable);

    Page<TourDTO> searchToursByTitle(String titlePart, Pageable pageable);
}
