package com.travelbuddy.exception;

import com.travelbuddy.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAllExceptions_ShouldReturnInternalServerError() {
        Exception ex = new RuntimeException("Test error");

        ResponseEntity<ErrorResponse> response = handler.handleAllExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("Произошла внутренняя ошибка сервера. Пожалуйста, обратитесь к администратору.",
                response.getBody().getMessage());
        assertTrue(response.getBody().getTimestamp() > 0);
    }

    @Test
    void handleAllExceptions_ShouldSetCorrectTimestamp() {
        Exception ex = new RuntimeException("Test error");
        long before = System.currentTimeMillis();

        ResponseEntity<ErrorResponse> response = handler.handleAllExceptions(ex);

        long after = System.currentTimeMillis();
        assertTrue(response.getBody().getTimestamp() >= before);
        assertTrue(response.getBody().getTimestamp() <= after);
    }

    @Test
    void handleAllExceptions_ShouldLogError() {

        Exception ex = new NullPointerException("Test NPE");

        ResponseEntity<ErrorResponse> response = handler.handleAllExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
