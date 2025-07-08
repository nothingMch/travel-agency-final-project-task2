package com.epam.finaltask.mapper.impl;

import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.model.Tour;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TourMapperImpl implements TourMapper {

    private final ModelMapper mapper;

    @Autowired
    public TourMapperImpl(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Tour toTour(TourDTO tourDTO) {
        return mapper.map(tourDTO, Tour.class);
    }

    @Override
    public TourDTO toTourDTO(Tour tour) {
        return mapper.map(tour, TourDTO.class);
    }
}
