package com.epam.finaltask.service;

import com.epam.finaltask.model.PasswordResetToken;
import com.epam.finaltask.model.User;
import jakarta.transaction.Transactional;

import java.util.Optional;

public interface PasswordResetService {
    @Transactional
    PasswordResetToken createToken(User user);

    Optional<User> validateToken(String token);

    @Transactional
    void deleteToken(String token);
}
