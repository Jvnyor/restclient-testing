package com.jvnyor.demorestclient.controllers.exceptions;

import com.jvnyor.demorestclient.controllers.exceptions.dtos.ErrorResponseDTO;
import com.jvnyor.demorestclient.services.exceptions.CatNotFoundException;
import com.jvnyor.demorestclient.services.exceptions.CatUnknownErrorException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    private ResponseEntity<Object> handleException(Exception exception, HttpServletRequest request) {

        final var internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(internalServerError)
                .body(new ErrorResponseDTO(
                        exception.getMessage(),
                        request.getRequestURI(),
                        exception.getClass().getSimpleName(),
                        internalServerError.value(),
                        LocalDateTime.now())
                );
    }

    @ExceptionHandler(CatUnknownErrorException.class)
    private ResponseEntity<Object> handleCatUnknownErrorException(CatUnknownErrorException exception, HttpServletRequest request) {

        final var internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(internalServerError)
                .body(new ErrorResponseDTO(
                        exception.getMessage(),
                        request.getRequestURI(),
                        exception.getClass().getSimpleName(),
                        internalServerError.value(),
                        LocalDateTime.now())
                );
    }

    @ExceptionHandler(RestClientException.class)
    private ResponseEntity<Object> handleRestClientException(RestClientException exception, HttpServletRequest request) {

        final var internalServerError = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(internalServerError)
                .body(new ErrorResponseDTO(
                        exception.getMessage(),
                        request.getRequestURI(),
                        exception.getClass().getSimpleName(),
                        internalServerError.value(),
                        LocalDateTime.now())
                );
    }

    @ExceptionHandler({NoResourceFoundException.class, CatNotFoundException.class})
    private ResponseEntity<Object> handleNotFoundExceptions(Exception exception, HttpServletRequest request) {

        final var notFound = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(notFound)
                .body(new ErrorResponseDTO(
                        exception.getMessage(),
                        request.getRequestURI(),
                        exception.getClass().getSimpleName(),
                        notFound.value(),
                        LocalDateTime.now())
                );
    }
}
