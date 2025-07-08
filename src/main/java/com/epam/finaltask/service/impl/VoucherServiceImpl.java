package com.epam.finaltask.service.impl;

import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.exception.custom.EntityNotFoundException;
import com.epam.finaltask.exception.custom.NotEnoughMoneyToPurchaseException;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.Tour;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.repository.TourRepository;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class VoucherServiceImpl implements VoucherService {

    private static final Logger logger = LoggerFactory.getLogger(VoucherServiceImpl.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourMapper tourMapper;

    @Autowired
    private VoucherMapper voucherMapper;

    @Override
    public VoucherDTO getById(String voucherId){
        logger.info("Fetching voucher with ID: {}", voucherId);
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));
        return voucherMapper.toVoucherDTO(voucher);
    }

    @Override
    @Transactional
    public TourDTO order(VoucherDTO voucherDTO) {
        logger.info("Placing an order for user ID: {} and tour ID: {}", voucherDTO.getUserId(), voucherDTO.getTourId());

        User user = userRepository.findById(UUID.fromString(voucherDTO.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Tour tour = tourRepository.findById(UUID.fromString(voucherDTO.getTourId()))
                .orElseThrow(() -> new EntityNotFoundException("Tour not found"));

        if (user.getBalance().compareTo(BigDecimal.valueOf(tour.getPrice())) < 0) {
            logger.warn("Insufficient balance for user ID {}", user.getId());
            throw new NotEnoughMoneyToPurchaseException("Insufficient balance");
        }

        Voucher voucher = new Voucher();
        voucher.setUser(user);
        voucher.setTour(tour);
        voucher.setStatus(VoucherStatus.PAID);

        user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(tour.getPrice())));

        userRepository.save(user);
        voucherRepository.save(voucher);

        logger.info("Voucher created and paid successfully with ID: {}", voucher.getId());

        return tourMapper.toTourDTO(tour);
    }


    @Override
    @Transactional
    public void cancelOrder(String voucherId){
        logger.info("Canceling order for voucher ID: {}", voucherId);
        String currentUsername = userService.getCurrentAuthenticatedUsername();
        UUID voucherID = UUID.fromString(voucherId);

        if(!voucherRepository.existsByUsernameAndVoucherId(currentUsername, voucherID))
            throw new AccessDeniedException("You are not authorized to cancel this voucher");

        voucherRepository.updateVoucherStatus(voucherID, VoucherStatus.CANCELED);
        logger.info("Voucher ID {} successfully canceled", voucherId);
    }

    @Override
    @Transactional
    public void cancelVoucher(UUID voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));

        if (voucher.getStatus() == VoucherStatus.CANCELED) {
            throw new IllegalStateException("Voucher is already canceled");
        }

        voucher.setStatus(VoucherStatus.CANCELED);

        User user = voucher.getUser();
        double newBalance = user.getBalance().doubleValue() + voucher.getTour().getPrice();
        user.setBalance(BigDecimal.valueOf(newBalance));
        userRepository.save(user);

        voucherRepository.save(voucher);
    }

    @Override
    @Transactional
    public void payOrder(String voucherId) {
        logger.info("Processing payment for voucher ID: {}", voucherId);
        String currentUsername = userService.getCurrentAuthenticatedUsername();

        User currentUser = userRepository.findUserByUsername(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Voucher voucher = voucherRepository.findById(UUID.fromString(voucherId))
                .orElseThrow(() -> new EntityNotFoundException("Voucher not found"));

        if(!voucher.getUser().getId().equals(currentUser.getId())){
            logger.warn("User ID {} not authorized to pay for voucher ID {}", currentUser.getId(), voucherId);
            throw new AccessDeniedException("You are not authorized to pay for this voucher");
        }

        double price = voucher.getTour().getPrice();
        if (currentUser.getBalance().doubleValue() < price) {
            logger.warn("Insufficient balance for user ID {}", currentUser.getId());
            throw new IllegalArgumentException("Insufficient balance");
        }

        voucher.setStatus(VoucherStatus.PAID);
        currentUser.setBalance(BigDecimal.valueOf(currentUser.getBalance().doubleValue() - price));

        userRepository.save(currentUser);
        voucherRepository.save(voucher);
        logger.info("Payment successfully processed for voucher ID: {}", voucherId);
    }

    @Override
    public void changeVoucherStatus(String voucherID, VoucherStatus newStatus){
        logger.info("Changing status of voucher ID {} to {}", voucherID, newStatus);
        UUID voucherUUID = UUID.fromString(voucherID);
        int updatedRows = voucherRepository.updateVoucherStatus(voucherUUID, newStatus);
        if (updatedRows == 0) {
            throw new EntityNotFoundException("Voucher not found");
        }
        logger.info("Voucher ID {} status successfully changed to {}", voucherID, newStatus);
    }

    @Override
    public Page<UserVoucherDTO> getVouchersByUserId(UUID userId, Pageable pageable) {
        logger.info("Fetching vouchers for user ID: {}, Page: {}", userId, pageable);

        Page<Voucher> vouchersPage = voucherRepository.findAllByUserId(userId, pageable);
        Page<UserVoucherDTO> result = vouchersPage.map(voucherMapper::toUserVoucherDTO);

        logger.info("Fetched {} vouchers for user ID: {}", result.getTotalElements(), userId);
        return result;
    }

    @Override
    public UserVoucherDTO getVoucherById(UUID voucherId) {
        logger.info("Fetching voucher by ID: {}", voucherId);

        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> {
                    logger.error("Voucher not found with ID: {}", voucherId);
                    return new EntityNotFoundException("Voucher not found with ID: " + voucherId);
                });

        UserVoucherDTO result = voucherMapper.toUserVoucherDTO(voucher);
        logger.info("Fetched voucher details: {}", result);
        return result;
    }

    @Override
    public void updateVoucherStatus(UUID id, VoucherStatus status) {
        logger.info("Updating status for voucher ID: {} to {}", id, status);

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Voucher not found with ID: {}", id);
                    return new EntityNotFoundException("Voucher not found with ID: " + id);
                });

        voucher.setStatus(status);
        voucherRepository.save(voucher);

        logger.info("Voucher ID {} status updated to {}", id, status);
    }

    @Override
    public Page<UserVoucherDTO> getAllVouchers(Pageable pageable) {
        logger.info("Fetching all vouchers, Page: {}", pageable);

        Page<UserVoucherDTO> result = voucherRepository.findAll(pageable)
                .map(voucherMapper::toUserVoucherDTO);

        logger.info("Fetched {} vouchers", result.getTotalElements());
        return result;
    }

    @Override
    public Page<UserVoucherDTO> getVouchersByStatus(VoucherStatus status, Pageable pageable) {
        logger.info("Fetching vouchers by status: {}, Page: {}", status, pageable);

        Page<UserVoucherDTO> result = voucherRepository.findByStatus(status, pageable)
                .map(voucherMapper::toUserVoucherDTO);

        logger.info("Fetched {} vouchers with status {}", result.getTotalElements(), status);
        return result;
    }
}
