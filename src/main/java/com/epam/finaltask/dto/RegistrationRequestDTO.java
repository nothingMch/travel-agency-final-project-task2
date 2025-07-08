package com.epam.finaltask.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequestDTO {

    @NotBlank(message = "{validation.registrationDTO.username.required}")
    private String username;

    @Email(message = "{validation.registrationDTO.emailValid}")
    private String email;

    @NotBlank(message = "{validation.registrationDTO.password.required}")
    @Size(min = 6, message = "{validation.registrationDTO.password.length}")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "{validation.registrationDTO.password.pattern}"
    )
    private String password;

    @NotBlank(message = "{validation.registrationDTO.repeatPassword.required}")
    @Size(min = 6, message = "{validation.registrationDTO.repeatPassword.length}")
    private String repeatedPassword;

    @NotBlank(message = "{validation.registrationDTO.phoneNumber.required}")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "{validation.registrationDTO.phoneNumber.pattern}")
    private String phoneNumber;

}
