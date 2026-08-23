package com.travelbuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.service.ApplicationService;
import com.travelbuddy.service.BudgetService;
import com.travelbuddy.service.JwtService;
import com.travelbuddy.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
@Import(JwtService.class)  // ← Подключаем JwtService
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripService tripService;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private BudgetService budgetService;

    private ObjectMapper objectMapper;
    private Trip testTrip;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        testTrip = new Trip();
        testTrip.setId(1L);
        testTrip.setFromPlace("Минск");
        testTrip.setToPlace("Браслав");
        testTrip.setDepartureDate(LocalDateTime.now().plusDays(3));
        testTrip.setTotalSeats(4);
        testTrip.setAvailableSeats(4);
    }

    @Test
    @WithMockUser  // ← ИМИТИРУЕМ АВТОРИЗОВАННОГО ПОЛЬЗОВАТЕЛЯ!
    void getTrips_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void createTrip_ShouldReturnOk() throws Exception {
        when(tripService.createTrip(any(Trip.class), any(String.class))).thenReturn(testTrip);

        mockMvc.perform(post("/api/trips")
                        .principal(() -> "driver@buddy.by")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrip)))
                .andExpect(status().isOk());
    }
}
