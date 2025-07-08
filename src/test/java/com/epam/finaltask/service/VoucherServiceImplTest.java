package com.epam.finaltask.service;

import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.custom.EntityNotFoundException;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.TourRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.service.impl.VoucherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VoucherServiceImplTest {

    @InjectMocks
    private VoucherServiceImpl voucherService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TourRepository tourRepository;

    @Mock
    private VoucherRepository voucherRepository;

    @Mock
    private UserService userService;

    @Mock
    private TourMapper tourMapper;

    @Mock
    private VoucherMapper voucherMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetById_VoucherExists_ReturnsVoucherDTO() {
        UUID voucherId = UUID.randomUUID();
        Voucher voucher = new Voucher();
        voucher.setId(voucherId);
        VoucherDTO voucherDTO = new VoucherDTO();
        when(voucherRepository.findById(voucherId)).thenReturn(Optional.of(voucher));
        when(voucherMapper.toVoucherDTO(voucher)).thenReturn(voucherDTO);

        VoucherDTO result = voucherService.getById(voucherId.toString());

        assertEquals(voucherDTO, result);
        verify(voucherRepository).findById(voucherId);
        verify(voucherMapper).toVoucherDTO(voucher);
    }

    @Test
    void testGetById_VoucherDoesNotExist_ThrowsException() {
        UUID voucherId = UUID.randomUUID();
        when(voucherRepository.findById(voucherId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> voucherService.getById(voucherId.toString()));
        verify(voucherRepository).findById(voucherId);
    }

    @Test
    void testOrder_ValidData_Success() {
        UUID userId = UUID.randomUUID();
        UUID tourId = UUID.randomUUID();
        VoucherDTO voucherDTO = new VoucherDTO();
        voucherDTO.setUserId(userId.toString());
        voucherDTO.setTourId(tourId.toString());

        User user = new User();
        user.setId(userId);
        user.setBalance(BigDecimal.valueOf(300));

        Tour tour = new Tour();
        tour.setId(tourId);
        tour.setPrice(300.0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(tourRepository.findById(tourId)).thenReturn(Optional.of(tour));
        when(tourMapper.toTourDTO(tour)).thenReturn(new TourDTO());

        TourDTO result = voucherService.order(voucherDTO);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(tourRepository).findById(tourId);
        verify(voucherRepository).save(any(Voucher.class));
    }

    @Test
    void testCancelOrder_UserAuthorized_Success() {
        UUID voucherId = UUID.randomUUID();
        String username = "testUser";
        when(userService.getCurrentAuthenticatedUsername()).thenReturn(username);
        when(voucherRepository.existsByUsernameAndVoucherId(username, voucherId)).thenReturn(true);

        voucherService.cancelOrder(voucherId.toString());

        verify(voucherRepository).updateVoucherStatus(voucherId, VoucherStatus.CANCELED);
    }

    @Test
    void testCancelOrder_UserNotAuthorized_ThrowsException() {
        UUID voucherId = UUID.randomUUID();
        String username = "testUser";
        when(userService.getCurrentAuthenticatedUsername()).thenReturn(username);
        when(voucherRepository.existsByUsernameAndVoucherId(username, voucherId)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> voucherService.cancelOrder(voucherId.toString()));
        verify(voucherRepository, never()).updateVoucherStatus(any(), any());
    }

    @Test
    void testPayOrder_ValidPayment_Success() {
        UUID voucherId = UUID.randomUUID();
        String username = "testUser";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setBalance(BigDecimal.valueOf(100));

        Tour tour = new Tour();
        tour.setPrice(50.0);

        Voucher voucher = new Voucher();
        voucher.setId(voucherId);
        voucher.setUser(user);
        voucher.setTour(tour);

        when(userService.getCurrentAuthenticatedUsername()).thenReturn(username);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(voucherRepository.findById(voucherId)).thenReturn(Optional.of(voucher));

        voucherService.payOrder(voucherId.toString());

        assertEquals(BigDecimal.valueOf(50.0), user.getBalance());
        assertEquals(VoucherStatus.PAID, voucher.getStatus());
        verify(userRepository).save(user);
        verify(voucherRepository).save(voucher);
    }

    @Test
    void testPayOrder_InsufficientBalance_ThrowsException() {
        UUID voucherId = UUID.randomUUID();
        String username = "testUser";

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setBalance(BigDecimal.valueOf(20));

        Tour tour = new Tour();
        tour.setPrice(50.0);

        Voucher voucher = new Voucher();
        voucher.setId(voucherId);
        voucher.setUser(user);
        voucher.setTour(tour);

        when(userService.getCurrentAuthenticatedUsername()).thenReturn(username);
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(voucherRepository.findById(voucherId)).thenReturn(Optional.of(voucher));

        assertThrows(IllegalArgumentException.class, () -> voucherService.payOrder(voucherId.toString()));
    }
}

