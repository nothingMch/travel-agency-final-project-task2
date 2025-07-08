package com.epam.finaltask.dto;

import java.util.List;
import java.util.Objects;

import com.epam.finaltask.model.Voucher;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class UserDTO {

	private String id;

	@NotBlank
	private String username;

	@NotBlank
	@Email(message = "Invalid email format")
	private String email;

	@NotBlank
	private String role;

	@NotBlank
	private String phoneNumber;

	@NotNull
	private Double balance;

	private boolean active;

	@Override
	public String toString() {
		return "UserDTO{" +
				"id='" + id + '\'' +
				", username='" + username + '\'' +
				", role='" + role + '\'' +
				", phoneNumber='" + phoneNumber + '\'' +
				", balance=" + balance +
				", active=" + active +
				'}';
	}
}
