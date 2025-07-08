package com.epam.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank(message = "{validation.LoginRequestDTO.username.required}")
    private String username;

    @NotBlank(message = "{validation.LoginRequestDTO.password.required}")
    private String password;

}