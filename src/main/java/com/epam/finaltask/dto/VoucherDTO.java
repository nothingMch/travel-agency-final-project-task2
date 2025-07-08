package com.epam.finaltask.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherDTO {

    private String id;

    @NotBlank
    private String userId;

    @NotBlank
    private String tourId;

    private String status;

}
