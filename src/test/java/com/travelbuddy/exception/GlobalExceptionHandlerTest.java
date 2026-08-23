package com.travelbuddy.exception;

import com.travelbuddy.dto.ErrorResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAllExceptions_ShouldReturn500() {
        ResponseEntity<ErrorResponseDto> response = handler.handleAllExceptions(new Exception("Test error"));

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("Произошла внутренняя ошибка сервера. Пожалуйста, обратитесь к администратору.",
                response.getBody().getMessage());
        assertTrue(response.getBody().getTimestamp() > 0);
    }

    @Test
    void handleRuntimeException_ShouldReturn400() {
        ResponseEntity<ErrorResponseDto> response = handler.handleRuntimeException(new RuntimeException("Неверный email"));

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().getStatus());
        assertEquals("Неверный email", response.getBody().getMessage());
    }
}
