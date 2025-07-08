package com.epam.finaltask.service;

import com.epam.finaltask.dto.FilterToursRequestDTO;
import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.repository.TourRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.service.impl.TourServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TourServiceImplTest {

    private final int PAGEABLE_PAGE = 0;
    private final int PAGEABLE_SIZE = 10;
    private final Pageable pageable = PageRequest.of(PAGEABLE_PAGE, PAGEABLE_SIZE);

    @Mock
    private UserRepository userRepository;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private TourMapper tourMapper;

    @InjectMocks
    private TourServiceImpl tourService;

    @Test
    void getTourById_TourExists_Success(){
        UUID id = UUID.randomUUID();
        Tour tour = new Tour();
        tour.setId(id);

        TourDTO expectedTourDTO = new TourDTO();
        expectedTourDTO.setId(id.toString());

        when(tourRepository.findById(id)).thenReturn(Optional.of(tour));
        when(tourMapper.toTourDTO(tour)).thenReturn(expectedTourDTO);

        TourDTO result = tourService.findById(id.toString());

        assertNotNull(result, "The returned TourDTO should not be null");
        assertEquals(expectedTourDTO.getId(), result.getId(), "The id should match the expected value");

        verify(tourRepository, times(1)).findById(any(UUID.class));
        verify(tourMapper, times(1)).toTourDTO(any(Tour.class));
    }

    @Test
    void getAll_Success(){
        List<Tour> tours = List.of(new Tour(), new Tour());
        Page<Tour> pageableTours = new PageImpl<>(tours, pageable, tours.size());

        List<TourDTO> expectedDtoList = List.of(new TourDTO(), new TourDTO());
        Page<TourDTO> expectedTours = new PageImpl<>(expectedDtoList, pageable, expectedDtoList.size());

        when(tourRepository.findAll(pageable)).thenReturn(pageableTours);
        when(tourMapper.toTourDTO(any(Tour.class))).thenAnswer(invocation -> new TourDTO());

        Page<TourDTO> result = tourService.findAll(pageable);

        assertNotNull(result, "The returned DTOs List should not be null");
        assertEquals(expectedTours.getContent().size(), result.getContent().size(), "The size of the content should match");
        assertEquals(expectedTours.getContent(), result.getContent(), "The content should match");

        verify(tourRepository, times(1)).findAll(any(Pageable.class));
        verify(tourMapper, times(2)).toTourDTO(any(Tour.class));
    }

    @Test
    void getAllByUserId_UserExists_Success(){
        UUID userId = UUID.randomUUID();
        List<Tour> tours = List.of(new Tour(), new Tour());
        Page<Tour> pageableTours = new PageImpl<>(tours, pageable, tours.size());

        List<TourDTO> expectedDtoList = List.of(new TourDTO(), new TourDTO());
        Page<TourDTO> expectedTours = new PageImpl<>(expectedDtoList, pageable, expectedDtoList.size());

        when(userRepository.existsById(userId)).thenReturn(true);
        when(tourRepository.findAllByUserId(userId, pageable)).thenReturn(pageableTours);
        when(tourMapper.toTourDTO(any(Tour.class))).thenAnswer(invocation -> new TourDTO());

        Page<TourDTO> result = tourService.findAllByUserId(userId.toString(), pageable);

        assertNotNull(result, "The returned DTOs List should not be null");
        assertEquals(expectedTours.getContent().size(), result.getContent().size(), "The size of the content should match");
        assertEquals(expectedTours.getContent(), result.getContent(), "The content should match");

        verify(userRepository, times(1)).existsById(any(UUID.class));
        verify(tourRepository, times(1)).findAllByUserId(any(UUID.class), any(Pageable.class));
        verify(tourMapper, times(2)).toTourDTO(any(Tour.class));
    }

    @Test
    void createTour_ValidData_Success(){
        String title = "tour for test";
        Tour tour = new Tour();
        tour.setTitle(title);

        TourDTO tourDTO = new TourDTO();
        tourDTO.setTitle(title);

        when(tourMapper.toTour(tourDTO)).thenReturn(tour);
        when(tourRepository.save(tour)).thenReturn(tour);
        when(tourMapper.toTourDTO(tour)).thenReturn(tourDTO);

        TourDTO result = tourService.create(tourDTO);

        assertNotNull(result, "The returned DTO should not be null");
        assertEquals(tourDTO, result);

        verify(tourRepository, times(1)).save(any(Tour.class));
        verify(tourMapper, times(1)).toTourDTO(any(Tour.class));
        verify(tourMapper, times(1)).toTour(any(TourDTO.class));
    }

    @Test
    void updateTour_ValidData_Success() {
        UUID tourId = UUID.randomUUID();
        String updatedTitle = "Updated Tour";
        TourDTO tourDTO = new TourDTO();
        tourDTO.setTitle(updatedTitle);

        Tour existingTour = new Tour();
        existingTour.setId(tourId);

        Tour updatedTour = new Tour();
        updatedTour.setId(tourId);
        updatedTour.setTitle(updatedTitle);

        when(tourRepository.existsById(tourId)).thenReturn(true);
        when(tourMapper.toTour(tourDTO)).thenReturn(updatedTour);
        when(tourRepository.save(updatedTour)).thenReturn(updatedTour);
        when(tourMapper.toTourDTO(updatedTour)).thenReturn(tourDTO);

        TourDTO result = tourService.update(tourId.toString(), tourDTO);

        assertNotNull(result, "The updated TourDTO should not be null");
        assertEquals(updatedTitle, result.getTitle(), "The title should be updated");
        verify(tourRepository, times(1)).existsById(tourId);
        verify(tourRepository, times(1)).save(updatedTour);
        verify(tourMapper, times(1)).toTour(tourDTO);
        verify(tourMapper, times(1)).toTourDTO(updatedTour);
    }


    @Test
    void deleteTour_TourExists_Success() {
        UUID tourId = UUID.randomUUID();

        when(tourRepository.existsById(tourId)).thenReturn(true);

        tourService.delete(tourId.toString());

        verify(tourRepository, times(1)).existsById(tourId);
        verify(tourRepository, times(1)).deleteById(tourId);
    }


    @Test
    void getAllByTourType_ValidData_Success() {
        TourType tourType = TourType.ADVENTURE;
        List<Tour> tours = List.of(new Tour(), new Tour());
        Page<Tour> pageableTours = new PageImpl<>(tours, pageable, tours.size());

        List<TourDTO> expectedDtoList = List.of(new TourDTO(), new TourDTO());
        Page<TourDTO> expectedTours = new PageImpl<>(expectedDtoList, pageable, expectedDtoList.size());

        when(tourRepository.findAllByTourType(tourType, pageable)).thenReturn(pageableTours);
        when(tourMapper.toTourDTO(any(Tour.class))).thenAnswer(invocation -> new TourDTO());

        Page<TourDTO> result = tourService.findAllByTourType(tourType, pageable);

        assertNotNull(result, "The returned DTOs List should not be null");
        assertEquals(expectedTours.getContent().size(), result.getContent().size(), "The size of the content should match");
        assertEquals(expectedTours.getContent(), result.getContent(), "The content should match");

        verify(tourRepository, times(1)).findAllByTourType(tourType, pageable);
        verify(tourMapper, times(2)).toTourDTO(any(Tour.class));
    }


    @Test
    void getAllByHotelType_ValidData_Success() {
        HotelType hotelType = HotelType.FIVE_STARS;
        List<Tour> tours = List.of(new Tour(), new Tour());
        Page<Tour> pageableTours = new PageImpl<>(tours, pageable, tours.size());

        List<TourDTO> expectedDtoList = List.of(new TourDTO(), new TourDTO());
        Page<TourDTO> expectedTours = new PageImpl<>(expectedDtoList, pageable, expectedDtoList.size());

        when(tourRepository.findAllByHotelType(hotelType, pageable)).thenReturn(pageableTours);
        when(tourMapper.toTourDTO(any(Tour.class))).thenAnswer(invocation -> new TourDTO());

        Page<TourDTO> result = tourService.findAllByHotelType(hotelType, pageable);

        assertNotNull(result, "The returned DTOs List should not be null");
        assertEquals(expectedTours.getContent().size(), result.getContent().size(), "The size of the content should match");
        assertEquals(expectedTours.getContent(), result.getContent(), "The content should match");

        verify(tourRepository, times(1)).findAllByHotelType(hotelType, pageable);
        verify(tourMapper, times(2)).toTourDTO(any(Tour.class));
    }


    @Test
    void getAllByTransferType_ValidData_Success() {
        TransferType transferType = TransferType.PLANE;
        List<Tour> tours = List.of(new Tour(), new Tour());
        Page<Tour> pageableTours = new PageImpl<>(tours, pageable, tours.size());

        List<TourDTO> expectedDtoList = List.of(new TourDTO(), new TourDTO());
        Page<TourDTO> expectedTours = new PageImpl<>(expectedDtoList, pageable, expectedDtoList.size());

        when(tourRepository.findAllByTransferType(transferType, pageable)).thenReturn(pageableTours);
        when(tourMapper.toTourDTO(any(Tour.class))).thenAnswer(invocation -> new TourDTO());

        Page<TourDTO> result = tourService.findAllByTransferType(transferType, pageable);

        assertNotNull(result, "The returned DTOs List should not be null");
        assertEquals(expectedTours.getContent().size(), result.getContent().size(), "The size of the content should match");
        assertEquals(expectedTours.getContent(), result.getContent(), "The content should match");

        verify(tourRepository, times(1)).findAllByTransferType(transferType, pageable);
        verify(tourMapper, times(2)).toTourDTO(any(Tour.class));
    }


    @Test
    void getAllByMinPrice_ValidData_Success() {
        Double minPrice = 100.0;
        List<Tour> tours = List.of(new Tour(), new Tour());
        Page<Tour> pageableTours = new PageImpl<>(tours, pageable, tours.size());

        List<TourDTO> expectedDtoList = List.of(new TourDTO(), new TourDTO());
        Page<TourDTO> expectedTours = new PageImpl<>(expectedDtoList, pageable, expectedDtoList.size());

        when(tourRepository.findByPriceGreaterThanEqual(minPrice, pageable)).thenReturn(pageableTours);
        when(tourMapper.toTourDTO(any(Tour.class))).thenAnswer(invocation -> new TourDTO());

        Page<TourDTO> result = tourService.findAllByMinPrice(minPrice, pageable);

        assertNotNull(result, "The returned DTOs List should not be null");
        assertEquals(expectedTours.getContent().size(), result.getContent().size(), "The size of the content should match");
        assertEquals(expectedTours.getContent(), result.getContent(), "The content should match");

        verify(tourRepository, times(1)).findByPriceGreaterThanEqual(minPrice, pageable);
        verify(tourMapper, times(2)).toTourDTO(any(Tour.class));
    }


    @Test
    void getFilteredTours_ValidData_Success() {
        FilterToursRequestDTO filter = new FilterToursRequestDTO();
        filter.setPrice(100.0);
        filter.setTourType("ADVENTURE");
        filter.setHotelType("FIVE_STARS");
        filter.setTransferType("PLANE");

        List<Tour> tours = List.of(new Tour(), new Tour());
        Page<Tour> pageableTours = new PageImpl<>(tours, pageable, tours.size());

        List<TourDTO> expectedDtoList = List.of(new TourDTO(), new TourDTO());
        Page<TourDTO> expectedTours = new PageImpl<>(expectedDtoList, pageable, expectedDtoList.size());

        when(tourRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageableTours);
        when(tourMapper.toTourDTO(any(Tour.class))).thenAnswer(invocation -> new TourDTO());

        Page<TourDTO> result = tourService.filterTours(filter, pageable);

        assertNotNull(result, "The returned DTOs List should not be null");
        assertEquals(expectedTours.getContent().size(), result.getContent().size(), "The size of the content should match");
        assertEquals(expectedTours.getContent(), result.getContent(), "The content should match");

        verify(tourRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(tourMapper, times(2)).toTourDTO(any(Tour.class));
    }

    @Test
    void searchToursByTitle_ValidData_Success() {
        String titlePart = "beach";
        List<Tour> tours = List.of(new Tour(), new Tour());
        Page<Tour> pageableTours = new PageImpl<>(tours, pageable, tours.size());

        List<TourDTO> expectedDtoList = List.of(new TourDTO(), new TourDTO());
        Page<TourDTO> expectedTours = new PageImpl<>(expectedDtoList, pageable, expectedDtoList.size());

        when(tourRepository.findByTitleContainingIgnoreCase(titlePart, pageable)).thenReturn(pageableTours);
        when(tourMapper.toTourDTO(any(Tour.class))).thenAnswer(invocation -> new TourDTO());

        Page<TourDTO> result = tourService.searchToursByTitle(titlePart, pageable);

        assertNotNull(result, "The returned DTOs List should not be null");
        assertEquals(expectedTours.getContent().size(), result.getContent().size(), "The size of the content should match");
        assertEquals(expectedTours.getContent(), result.getContent(), "The content should match");

        verify(tourRepository, times(1)).findByTitleContainingIgnoreCase(titlePart, pageable);
        verify(tourMapper, times(2)).toTourDTO(any(Tour.class));
    }


}
