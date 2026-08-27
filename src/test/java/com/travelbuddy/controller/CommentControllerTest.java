package com.travelbuddy.controller;

import com.travelbuddy.dto.AuthRequestDto;
import com.travelbuddy.dto.CommentRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommentControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private String token;

    @BeforeEach
    void setUp() {
        AuthRequestDto loginDto = new AuthRequestDto();
        loginDto.setEmail("alice@buddy.by");
        loginDto.setPassword("qwerty123");

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/api/auth/login",
                loginDto,
                String.class
        );

        token = loginResponse.getBody().replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
    }

    @Test
    void addComment_ShouldReturnOk() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setMessage("Интеграционный комментарий");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<CommentRequestDto> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trips/1/comments",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
