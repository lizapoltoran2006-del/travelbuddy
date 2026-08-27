package com.travelbuddy.controller;

import com.travelbuddy.dto.AuthRequestDto;
import com.travelbuddy.dto.ReviewRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewControllerTest {

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

    /* @Test
    void addReview_ShouldReturnOk() {
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setRating(5);
        dto.setText("Интеграционный отзыв");
        dto.setTargetUserId(2L); // ← Борис, а не Алиса!

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<ReviewRequestDto> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/reviews",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }*/
}