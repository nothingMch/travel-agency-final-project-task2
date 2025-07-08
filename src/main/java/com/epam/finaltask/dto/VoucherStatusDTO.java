package com.epam.finaltask.dto;

import com.epam.finaltask.dto.validation.ValidEnum;
import com.epam.finaltask.model.VoucherStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherStatusDTO {

    @ValidEnum(enumClass = VoucherStatus.class)
    @NotBlank
    private String voucherStatus;

}
