package com.epam.finaltask.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"User\"")
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
    private UUID id;

	@Column(name = "username")
    private String username;

	@Column(name = "password")
    private String password;

	@Column(name = "email")
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
    private Role role;

	@OneToMany(mappedBy = "user")
    private List<Voucher> vouchers;

	@Column(name = "phone_number")
    private String phoneNumber;

	@Column(name = "balance")
    private BigDecimal balance;

	@Column(name = "account_status")
    private boolean active;

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", role=" + role +
				", vouchers=" + vouchers +
				", phoneNumber='" + phoneNumber + '\'' +
				", balance=" + balance +
				", active=" + active +
				'}';
	}
}