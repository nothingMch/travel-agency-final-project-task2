package com.epam.finaltask.service.config;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("securityService")
public class SecurityService {

    private final UserRepository userRepository;

    private final VoucherRepository voucherRepository;

    public SecurityService(UserRepository someEntityRepository,
                           VoucherRepository voucherRepository) {
        this.userRepository = someEntityRepository;
        this.voucherRepository = voucherRepository;
    }

    public boolean isOwner(UUID voucherId, Authentication authentication) {
        Optional<Voucher> voucherOpt = voucherRepository.findById(voucherId);
        if (voucherOpt.isEmpty()) {
            return false;
        }
        Voucher voucher = voucherOpt.get();

        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();

        return voucher.getUser().getId().equals(user.getId());
    }
}