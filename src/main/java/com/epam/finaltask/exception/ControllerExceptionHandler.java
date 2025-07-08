package com.epam.finaltask.exception;

import com.epam.finaltask.exception.custom.CurrentUserNotFoundException;
import com.epam.finaltask.exception.custom.NotEnoughMoneyToPurchaseException;
import com.epam.finaltask.exception.custom.UpdateUserException;
import com.epam.finaltask.exception.custom.UserRegistrationException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice(basePackages = "com.epam.finaltask.controller.mvc")
public class ControllerExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(UserRegistrationException.class)
    public String handleUserRegistrationException(UserRegistrationException ex, RedirectAttributes redirectAttributes) {
        logger.error("User registration error: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/auth/registration";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model, HttpServletRequest request) {
        logger.error("Unhandled exception at path {}: {}", request.getRequestURI(), ex.getMessage());

        model.addAttribute("status", getStatus(request));
        model.addAttribute("error", ex.getClass().getSimpleName());
        model.addAttribute("message", ex.getMessage());
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", System.currentTimeMillis());
        model.addAttribute("exception", ex.getClass().getName());
        model.addAttribute("cause", ex.getCause() != null ? ex.getCause().toString() : "Unknown");

        return "error";
    }

    @ExceptionHandler(NotEnoughMoneyToPurchaseException.class)
    public String handleNotEnoughMoneyException(NotEnoughMoneyToPurchaseException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Not enough money to purchase: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("warning", "Insufficient balance to purchase the tour. Please top up your account.");
        return "redirect:/profile";
    }

    @ExceptionHandler(UpdateUserException.class)
    public String handleUpdateUserException(UpdateUserException ex, RedirectAttributes redirectAttributes) {
        logger.warn("User update error: {}", ex.getMessage());

        if (ex.getFieldErrors() != null) {
            redirectAttributes.addFlashAttribute("validationErrors", ex.getFieldErrors());
        }
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/profile";
    }

    @ExceptionHandler(CurrentUserNotFoundException.class)
    public String handleEntityNotFoundException(CurrentUserNotFoundException ex) {
        logger.warn("Current user not found: {}", ex.getMessage());
        return "redirect:/auth/login";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request,
            Model model) {

        logger.error("Validation error at path {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : fieldErrors) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("error", "Validation Error");
        model.addAttribute("message", "Invalid input data");
        model.addAttribute("path", request.getRequestURI());
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("exception", ex.getClass().getName());
        model.addAttribute("cause", errors);

        return "error";
    }

    private int getStatus(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            return Integer.parseInt(status.toString());
        }
        return 500;
    }

}
