package com.epam.finaltask.service;

import com.epam.finaltask.dto.TourDTO;
import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.VoucherStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface VoucherService {

    VoucherDTO getById(String voucherId);

    public TourDTO order(VoucherDTO voucherDTO);

    @Transactional
    void cancelOrder(String voucherId);

    @Transactional
    void cancelVoucher(UUID voucherId);

    @Transactional
    void payOrder(String voucherId);

    void changeVoucherStatus(String voucherID, VoucherStatus newStatus);

    Page<UserVoucherDTO> getVouchersByUserId(UUID userId, Pageable pageable);

    UserVoucherDTO getVoucherById(UUID voucherId);

    void updateVoucherStatus(UUID id, VoucherStatus status);

    Page<UserVoucherDTO> getAllVouchers(Pageable pageable);

    Page<UserVoucherDTO> getVouchersByStatus(VoucherStatus status, Pageable pageable);
}
