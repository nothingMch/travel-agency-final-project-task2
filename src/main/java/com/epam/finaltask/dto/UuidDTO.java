package com.epam.finaltask.dto;

import com.epam.finaltask.dto.validation.ValidUuid;

public class UuidDTO {

    @ValidUuid
    public String uuid;

}
