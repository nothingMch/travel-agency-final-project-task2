package com.epam.finaltask.controller.mvc;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.exception.custom.CurrentUserNotFoundException;
import com.epam.finaltask.exception.custom.EntityNotFoundException;
import com.epam.finaltask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "com.epam.finaltask.controller.mvc")
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute("currentUser")
    public UserDTO populateCurrentUser() {
        try {
            String username = userService.getCurrentAuthenticatedUsername();
            return userService.getUserByUsername(username);
        } catch (EntityNotFoundException | CurrentUserNotFoundException ex) {
            return null;
        }
    }
}
