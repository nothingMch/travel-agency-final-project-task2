package com.epam.finaltask.controller.rest;

import com.epam.finaltask.dto.RegistrationRequestDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody RegistrationRequestDTO registrationDTO){
        UserDTO userDTO = userService.register(registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

}
