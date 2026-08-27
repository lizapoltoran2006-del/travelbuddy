package com.travelbuddy.controller;

import com.travelbuddy.dto.AuthRequestDto;
import com.travelbuddy.dto.TripRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TripControllerTest {

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
    void createTrip_ShouldReturnOk() {
        TripRequestDto dto = new TripRequestDto();
        dto.setFromPlace("Минск");
        dto.setToPlace("Браслав");
        dto.setDepartureDate(LocalDateTime.now().plusDays(3));
        dto.setTotalSeats(4);
        dto.setDescription("Интеграционный тест");
        dto.setPaymentDetails("Карта: 1234");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<TripRequestDto> request = new HttpEntity<>(dto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/trips",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Минск"));
    }
}
