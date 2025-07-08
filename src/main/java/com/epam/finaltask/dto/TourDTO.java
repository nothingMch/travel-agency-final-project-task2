package com.epam.finaltask.dto;

import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.dto.validation.ValidEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@EqualsAndHashCode
public class TourDTO {

    private String id;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "Price is required")
    private Double price;

    @NotBlank(message = "tourType is required")
    @ValidEnum(enumClass = TourType.class)
    private String tourType;

    @NotBlank(message = "transferType is required")
    @ValidEnum(enumClass = TransferType.class)
    private String transferType;

    @NotBlank(message = "hotelType is required")
    @ValidEnum(enumClass = HotelType.class)
    private String hotelType;

    @NotNull(message = "arrivalDate is required")
    private LocalDate arrivalDate;

    @NotNull(message = "evictionDate is required")
    private LocalDate evictionDate;

    private boolean hot;

}
