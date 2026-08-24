package com.travelbuddy.controller;

import com.travelbuddy.dto.AuthRequestDto;
import com.travelbuddy.dto.RegisterRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void register_ShouldReturnOk() {
        String uniqueEmail = "test_" + System.currentTimeMillis() + "@buddy.by";

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail(uniqueEmail);
        dto.setPassword("password");
        dto.setFullName("Test Integration");
        dto.setAge(25);
        dto.setContactInfo("@testint");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register",
                dto,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains(uniqueEmail));
    }

    @Test
    void login_ShouldReturnOk() {
        // 1. Создаём уникального пользователя для логина
        String uniqueEmail = "test_login_" + System.currentTimeMillis() + "@buddy.by";

        RegisterRequestDto registerDto = new RegisterRequestDto();
        registerDto.setEmail(uniqueEmail);
        registerDto.setPassword("password");
        registerDto.setFullName("Test Login");
        registerDto.setAge(25);
        registerDto.setContactInfo("@testlogin");

        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                "/api/auth/register",
                registerDto,
                String.class
        );
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        // 2. Логинимся
        AuthRequestDto loginDto = new AuthRequestDto();
        loginDto.setEmail(uniqueEmail);
        loginDto.setPassword("password");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/api/auth/login",
                loginDto,
                String.class
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertTrue(loginResponse.getBody().contains("token"));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalid() {
        // Используем заведомо несуществующий email
        AuthRequestDto dto = new AuthRequestDto();
        dto.setEmail("nonexistent_" + System.currentTimeMillis() + "@buddy.by");
        dto.setPassword("wrong");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login",
                dto,
                String.class
        );

        // Сервер должен вернуть 400 Bad Request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Проверяем, что в ответе есть сообщение об ошибке
        assertTrue(response.getBody().contains("Неверный email или пароль") ||
                response.getBody().contains("Пользователь не найден"));
    }
}
