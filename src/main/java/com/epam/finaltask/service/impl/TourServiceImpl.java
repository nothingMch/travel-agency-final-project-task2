package com.epam.finaltask.service.impl;

import com.epam.finaltask.dto.FilterToursRequestDTO;
import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.exception.custom.EntityNotFoundException;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.Specification.TourSpecifications;
import com.epam.finaltask.repository.TourRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.service.TourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TourServiceImpl implements TourService {

    private static final Logger logger = LoggerFactory.getLogger(TourServiceImpl.class);

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourMapper tourMapper;

    @Override
    public TourDTO create(TourDTO tourDTO) {
        logger.info("Creating tour with data: {}", tourDTO);
        Tour tour = tourRepository.save(tourMapper.toTour(tourDTO));
        TourDTO result = tourMapper.toTourDTO(tour);
        logger.info("Tour created successfully: {}", result);
        return result;
    }

    @Override
    public TourDTO update(String tourId, TourDTO tourDTO) {
        logger.info("Updating tour with ID: {} and data: {}", tourId, tourDTO);
        isTourExist(tourId);
        Tour tour = tourMapper.toTour(tourDTO);
        tour.setId(UUID.fromString(tourId));
        TourDTO result = tourMapper.toTourDTO(tourRepository.save(tour));
        logger.info("Tour updated successfully: {}", result);
        return result;
    }

    @Override
    public void delete(String tourId) {
        logger.info("Deleting tour with ID: {}", tourId);
        isTourExist(tourId);
        tourRepository.deleteById(UUID.fromString(tourId));
        logger.info("Tour deleted successfully: {}", tourId);
    }

    @Override
    public void changeHotStatus(UUID tourId, boolean isHot) {
        logger.info("Changing hot status for tour ID: {} to {}", tourId, isHot);
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new EntityNotFoundException("Tour not found"));
        tour.setHot(isHot);
        tourRepository.save(tour);
        logger.info("Hot status updated successfully for tour ID: {}", tourId);
    }

    @Override
    public Page<TourDTO> findAllByUserId(String userId, Pageable pageable) {
        logger.info("Finding all tours by user ID: {}, Pageable: {}", userId, pageable);
        if (!userRepository.existsById(UUID.fromString(userId))) {
            throw new EntityNotFoundException("User not found");
        }
        Page<TourDTO> result = tourRepository.findAllByUserId(UUID.fromString(userId), pageable)
                .map(tourMapper::toTourDTO);
        logger.info("Found tours by user ID: {}, Result: {}", userId, result);
        return result;
    }

    @Override
    public Page<TourDTO> findAllByTourType(TourType tourType, Pageable pageable) {
        logger.info("Finding all tours by tour type: {}, Pageable: {}", tourType, pageable);
        Page<TourDTO> result = tourRepository.findAllByTourType(tourType, pageable)
                .map(tourMapper::toTourDTO);
        logger.info("Found tours by tour type: {}, Result: {}", tourType, result);
        return result;
    }

    @Override
    public Page<TourDTO> findAllByTransferType(TransferType transferType, Pageable pageable) {
        logger.info("Finding all tours by transfer type: {}, Pageable: {}", transferType, pageable);
        Page<TourDTO> result = tourRepository.findAllByTransferType(transferType, pageable)
                .map(tourMapper::toTourDTO);
        logger.info("Found tours by transfer type: {}, Result: {}", transferType, result);
        return result;
    }

    @Override
    public Page<TourDTO> findAllByMinPrice(Double price, Pageable pageable) {
        logger.info("Finding all tours by minimum price: {}, Pageable: {}", price, pageable);
        Page<TourDTO> result = tourRepository.findByPriceGreaterThanEqual(price, pageable)
                .map(tourMapper::toTourDTO);
        logger.info("Found tours by minimum price: {}, Result: {}", price, result);
        return result;
    }

    @Override
    public Page<TourDTO> findAllByHotelType(HotelType hotelType, Pageable pageable) {
        logger.info("Finding all tours by hotel type: {}, Pageable: {}", hotelType, pageable);
        Page<TourDTO> result = tourRepository.findAllByHotelType(hotelType, pageable)
                .map(tourMapper::toTourDTO);
        logger.info("Found tours by hotel type: {}, Result: {}", hotelType, result);
        return result;
    }

    @Override
    public Page<TourDTO> findAll(Pageable pageable) {
        logger.info("Finding all tours with Pageable: {}", pageable);
        Page<TourDTO> result = tourRepository.findAll(pageable)
                .map(tourMapper::toTourDTO);
        logger.info("Found tours: {}", result);
        return result;
    }

    @Override
    public TourDTO findById(String id) {
        logger.info("Finding tour by ID: {}", id);
        TourDTO result = tourMapper.toTourDTO(tourRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Tour not found")));
        logger.info("Found tour by ID: {}, Result: {}", id, result);
        return result;
    }

    @Override
    public Page<TourDTO> filterTours(FilterToursRequestDTO filter, Pageable pageable) {
        logger.info("Filtering tours with filter: {}, Pageable: {}", filter, pageable);
        Specification<Tour> specification = Specification
                .where(TourSpecifications.hasPriceGreaterThanOrEqualTo(filter.getPrice()))
                .and(TourSpecifications.hasTourType(TourType.valueOf(filter.getTourType())))
                .and(TourSpecifications.hasHotelType(HotelType.valueOf(filter.getHotelType())))
                .and(TourSpecifications.hasTransferType(TransferType.valueOf(filter.getTransferType())));

        Page<TourDTO> result = tourRepository.findAll(specification, pageable)
                .map(tourMapper::toTourDTO);
        logger.info("Filtered tours: {}", result);
        return result;
    }

    @Override
    public Page<TourDTO> getFilteredTours(String search, FilterToursRequestDTO filter, Pageable pageable) {
        logger.info("Getting filtered tours with search: {}, filter: {}, Pageable: {}", search, filter, pageable);
        Specification<Tour> specification = Specification.where(null);

        if (search != null && !search.isBlank()) {
            specification = specification.and(TourSpecifications.titleContains(search));
        }

        if (filter.getPrice() != null) {
            specification = specification.and(TourSpecifications.hasPriceGreaterThanOrEqualTo(filter.getPrice()));
        }
        if (filter.getTourType() != null && !filter.getTourType().isBlank()) {
            specification = specification.and(TourSpecifications.hasTourType(TourType.valueOf(filter.getTourType())));
        }
        if (filter.getTransferType() != null && !filter.getTransferType().isBlank()) {
            specification = specification.and(TourSpecifications.hasTransferType(TransferType.valueOf(filter.getTransferType())));
        }
        if (filter.getHotelType() != null && !filter.getHotelType().isBlank()) {
            specification = specification.and(TourSpecifications.hasHotelType(HotelType.valueOf(filter.getHotelType())));
        }

        Page<TourDTO> result = tourRepository.findAll(specification, pageable).map(tourMapper::toTourDTO);
        logger.info("Filtered tours: {}", result);
        return result;
    }

    @Override
    public Page<TourDTO> searchToursByTitle(String titlePart, Pageable pageable) {
        logger.info("Searching tours by title part: {}, Pageable: {}", titlePart, pageable);
        Page<TourDTO> result = tourRepository.findByTitleContainingIgnoreCase(titlePart, pageable)
                .map(tourMapper::toTourDTO);
        logger.info("Found tours by title part: {}, Result: {}", titlePart, result);
        return result;
    }

    private void isTourExist(String tourId) {
        logger.info("Checking if tour exists with ID: {}", tourId);
        if (!tourRepository.existsById(UUID.fromString(tourId))) {
            logger.warn("Tour not found with ID: {}", tourId);
            throw new EntityNotFoundException("Tour not found");
        }
        logger.info("Tour exists with ID: {}", tourId);
    }
}
