package com.epam.finaltask.service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendResetLink(String toEmail, String token) {
        String resetLink = "https://localhost:8443/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the following link:\n" + resetLink);

        try {
            mailSender.send(message);
            logger.info("Password reset email sent to {}", toEmail);
            return true;
        } catch (MailException e) {
            logger.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }
}
