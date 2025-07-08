package com.epam.finaltask.service.impl;

import com.epam.finaltask.model.PasswordResetToken;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.PasswordResetTokenRepository;
import com.epam.finaltask.service.PasswordResetService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordResetServiceImpl implements PasswordResetService{
    private final PasswordResetTokenRepository tokenRepository;

    @Autowired
    public PasswordResetServiceImpl(PasswordResetTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    @Override
    public PasswordResetToken createToken(User user) {
        PasswordResetToken token = new PasswordResetToken(user);
        return tokenRepository.save(token);
    }

    @Override
    public Optional<User> validateToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isExpired())
                .map(PasswordResetToken::getUser);
    }

    @Transactional
    @Override
    public void deleteToken(String token) {
        tokenRepository.deleteById(token);
    }
}
