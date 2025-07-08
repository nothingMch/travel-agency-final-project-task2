package com.epam.finaltask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {

    @NotBlank(message = "Username cannot be blank")
    @Size(max = 50)
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 50)
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Size(max = 15)
    private String phoneNumber;
}