package com.epam.finaltask.mapper.impl;

import com.epam.finaltask.dto.UserVoucherDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.Voucher;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class VoucherMapperImpl implements VoucherMapper {

    private final ModelMapper mapper;

    @Autowired
    public VoucherMapperImpl(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Voucher toVoucher(VoucherDTO voucherDTO) {
        return mapper.map(voucherDTO, Voucher.class);
    }

    @Override
    public VoucherDTO toVoucherDTO(Voucher voucher) {
        return mapper.map(voucher, VoucherDTO.class);
    }

    @Override
    public UserVoucherDTO toUserVoucherDTO(Voucher voucher) {
        UserVoucherDTO dto = new UserVoucherDTO();
        dto.setId(voucher.getId().toString());
        dto.setUserId(voucher.getUser().getId().toString());
        dto.setTourId(voucher.getTour().getId().toString());
        dto.setTourTitle(voucher.getTour().getTitle());
        dto.setTourPrice(BigDecimal.valueOf(voucher.getTour().getPrice()));
        dto.setVoucherStatus(voucher.getStatus().name());
        return dto;
    }
}
