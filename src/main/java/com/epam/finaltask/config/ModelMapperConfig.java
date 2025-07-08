package com.epam.finaltask.config;

import com.epam.finaltask.dto.RegistrationRequestDTO;
import com.epam.finaltask.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.addMappings(new PropertyMap<RegistrationRequestDTO, User>() {
            @Override
            protected void configure() {
                skip(destination.getId());
                skip(destination.getPassword());
                skip(destination.getRole());
                skip(destination.getBalance());
                skip(destination.getVouchers());
                skip(destination.isActive());
            }
        });

        return modelMapper;
    }
}
