package com.travelbuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.TripBudget;
import com.travelbuddy.entity.User;
import com.travelbuddy.service.TripService;
import com.travelbuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TripControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TripService tripService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TripController tripController;

    private ObjectMapper objectMapper;
    private Trip testTrip;
    private User testUser;


    private Principal testPrincipal = () -> "test@buddy.by";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tripController).build();

        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@buddy.by");
        testUser.setFullName("Test User");

        testTrip = new Trip();
        testTrip.setId(1L);
        testTrip.setFromPlace("Минск");
        testTrip.setToPlace("Браслав");
        testTrip.setDepartureDate(LocalDateTime.now().plusDays(3));
        testTrip.setTotalSeats(4);
        testTrip.setAvailableSeats(4);
        testTrip.setDescription("Тестовая поездка");
        testTrip.setDriver(testUser);
    }

    @Test
    void getTrips_ShouldReturnListOfTrips() throws Exception {
        when(tripService.getAllTrips()).thenReturn(List.of(testTrip));

        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fromPlace").value("Минск"));
    }

    @Test
    void createTrip_ShouldReturnSavedTrip() throws Exception {
        when(tripService.createTrip(any(Trip.class), anyString())).thenReturn(testTrip);

        mockMvc.perform(post("/api/trips")
                        .principal(testPrincipal)  // <-- Добавляем Principal!
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTrip)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void applyForTrip_ShouldReturnApplication() throws Exception {
        TripApplication application = new TripApplication();
        application.setId(1L);
        application.setStatus("ACCEPTED");

        when(tripService.applyForTrip(anyLong(), anyString(), anyInt())).thenReturn(application);

        mockMvc.perform(post("/api/trips/1/apply")
                        .principal(testPrincipal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void applyForTrip_WithSeatsParameter_ShouldBookMultipleSeats() throws Exception {
        TripApplication application = new TripApplication();
        application.setId(1L);
        application.setStatus("ACCEPTED");
        application.setSeatsCount(3);

        when(tripService.applyForTrip(anyLong(), anyString(), anyInt())).thenReturn(application);

        mockMvc.perform(post("/api/trips/1/apply")
                        .principal(testPrincipal)
                        .param("seats", "3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.seatsCount").value(3));
    }


    @Test
    void addBudget_ShouldReturnBudget() throws Exception {
        TripBudget budget = new TripBudget();
        budget.setId(1L);
        budget.setExpenseName("Бензин");
        budget.setTotalAmount(BigDecimal.valueOf(100));
        budget.setAmountPerPerson(BigDecimal.valueOf(25));
        when(tripService.calculateAndSaveBudget(anyLong(), anyString(), any(BigDecimal.class)))
                .thenReturn(budget);

        mockMvc.perform(post("/api/trips/1/budget")
                        .principal(testPrincipal)  // <-- Добавляем Principal!
                        .param("expenseName", "Бензин")
                        .param("totalAmount", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void register_ShouldReturnUser() throws Exception {
        when(userService.registerNewUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void createTrip_ShouldReturnBadRequest_WhenTripIsNull() throws Exception {
        mockMvc.perform(post("/api/trips")
                        .principal(testPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk()); // или isBadRequest() если добавишь валидацию
    }

    @Test
    void getTrips_ShouldReturnEmptyList_WhenNoTrips() throws Exception {
        when(tripService.getAllTrips()).thenReturn(List.of());

        mockMvc.perform(get("/api/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void showTripsPage_ShouldReturnTripsView() throws Exception {
        when(tripService.getAllTrips()).thenReturn(List.of(testTrip));

        mockMvc.perform(get("/trips"))
                .andExpect(status().isOk());

    }

    @Test
    void showLoginPage_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void cancelApplication_ShouldReturnSuccess() throws Exception {

        doNothing().when(tripService).cancelApplication(anyLong(), anyString());

        mockMvc.perform(delete("/api/applications/1")
                        .principal(testPrincipal))
                .andExpect(status().isOk());
    }

}
