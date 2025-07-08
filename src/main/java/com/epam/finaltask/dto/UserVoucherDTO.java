package com.epam.finaltask.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class UserVoucherDTO {

    private String id;

    private String userId;

    private String tourId;

    private String tourTitle;

    private BigDecimal tourPrice;

    private String voucherStatus;

}