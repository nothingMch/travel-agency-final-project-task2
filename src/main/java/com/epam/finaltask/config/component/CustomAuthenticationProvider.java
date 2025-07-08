package com.epam.finaltask.config.component;

import com.epam.finaltask.service.config.CustomUserDetailsService;
import com.epam.finaltask.service.config.LoginAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    private final CustomUserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(CustomUserDetailsService userDetailsService,
                                        LoginAttemptService loginAttemptService,
                                        PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.loginAttemptService = loginAttemptService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();

        if (loginAttemptService.isBlocked(username)) {
            logger.info("User account is locked due to too many failed login attempts");
            throw new LockedException("User account is locked due to too many failed login attempts");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String presentedPassword = authentication.getCredentials().toString();

        if (!userDetails.isEnabled()) {
            logger.info("User account is blocked");
            throw new LockedException("User account is blocked");
        }
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            logger.info("Invalid credentials");
            loginAttemptService.loginFailed(username);
            throw new BadCredentialsException("Invalid credentials");
        }

        loginAttemptService.loginSucceeded(username);

        return new UsernamePasswordAuthenticationToken(userDetails, presentedPassword, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}