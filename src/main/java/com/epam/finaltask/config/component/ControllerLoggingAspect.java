package com.epam.finaltask.config.component;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class ControllerLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ControllerLoggingAspect.class);

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || " +
            "@within(org.springframework.stereotype.Controller)")
    public void controllerMethods() {}

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) ||" +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void mappingAnnotations() {}

    @Before("controllerMethods() && mappingAnnotations()")
    public void logHttpMappings(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String uri = request.getRequestURI();

        String methodName = joinPoint.getSignature().getName();
        String args = filterArguments(joinPoint.getArgs());

        logger.info("Request made: [HTTP Method: {}, URI: {}, Method: {}, Arguments: {}]",
                request.getMethod(), uri, methodName, args);
    }

    private String filterArguments(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    String str = arg.toString();
                    return str.length() > 200 ? arg.getClass().getSimpleName() + "(too large)" : str;
                })
                .reduce((a, b) -> a + ", " + b)
                .orElse("no args");
    }

    @AfterReturning(pointcut = "controllerMethods() && mappingAnnotations()", returning = "response")
    public void logResponse(JoinPoint joinPoint, Object response) {
        String methodName = joinPoint.getSignature().getName();

        if (response instanceof String) {
            logger.info("Response from method: {}, View name: {}", methodName, response);
        } else if (response instanceof org.springframework.web.servlet.ModelAndView) {
            logger.info("Response from method: {}, ModelAndView: {}", methodName, response);
        } else {
            logger.info("Response from method: {}, Response data: {}", methodName, response);
        }
    }
}