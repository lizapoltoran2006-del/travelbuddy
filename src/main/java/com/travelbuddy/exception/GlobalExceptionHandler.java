package com.travelbuddy.exception;

import com.travelbuddy.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(Exception ex) {
        log.error("Фатальная ошибка в приложении: ", ex);

        ErrorResponseDto error = new ErrorResponseDto(
                "Произошла внутренняя ошибка сервера. Пожалуйста, обратитесь к администратору.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException ex) {
        log.warn("Ошибка выполнения: {}", ex.getMessage());

        ErrorResponseDto error = new ErrorResponseDto(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
