package com.epam.finaltask.mapper.impl;

import com.epam.finaltask.dto.RegistrationRequestDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserMapperImpl implements UserMapper {

    private final ModelMapper mapper;

    @Autowired
    public UserMapperImpl(ModelMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public User toUser(UserDTO userDTO) {
        return mapper.map(userDTO, User.class);
    }

    @Override
    public UserDTO toUserDTO(User user) {
        return mapper.map(user, UserDTO.class);
    }

    @Override
    public User toUserFromRegistration(RegistrationRequestDTO registrationRequestDTO, String encodedPassword) {
        User user = mapper.map(registrationRequestDTO, User.class);
        user.setPassword(encodedPassword);
        user.setRole(Role.USER);
        user.setBalance(BigDecimal.ZERO);
        user.setActive(true);
        return user;
    }
}
