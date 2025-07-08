package com.epam.finaltask.dto;

import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.dto.validation.ValidEnum;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterToursRequestDTO {

    @Min(value = 100, message = "{validation.FilterToursRequestDTO.price.minNumber}")
    private Double price;

    @ValidEnum(enumClass = TourType.class, message = "{validation.FilterToursRequestDTO.tourType.validEnum}")
    private String tourType;

    @ValidEnum(enumClass = TransferType.class, message = "{validation.FilterToursRequestDTO.transferType.validEnum}")
    private String transferType;

    @ValidEnum(enumClass = HotelType.class, message = "{validation.FilterToursRequestDTO.hotelType.validEnum}")
    private String hotelType;

}
