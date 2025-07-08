package com.epam.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {

    @NotBlank(message = "{validation.changePasswordDTO.oldPassword.required}")
    private String oldPassword;

    @NotBlank(message = "{validation.changePasswordDTO.newPassword.required}")
    @Size(min = 6, message = "{validation.changePasswordDTO.newPassword.length}")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "{validation.changePasswordDTO.newPassword.pattern}"
    )
    private String newPassword;

    @NotBlank(message = "{validation.changePasswordDTO.confirmNewPassword.required}")
    @Size(min = 6, message = "{validation.changePasswordDTO.confirmNewPassword.length}")
    private String confirmNewPassword;
}