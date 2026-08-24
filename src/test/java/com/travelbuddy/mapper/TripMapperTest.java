package com.travelbuddy.mapper;

import com.travelbuddy.dto.TripRequestDto;
import com.travelbuddy.entity.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TripMapperTest {

    private TripMapper tripMapper;

    @BeforeEach
    void setUp() {
        tripMapper = new TripMapper();
    }

    @Test
    void toEntity_ShouldMapAllFields() {
        TripRequestDto dto = new TripRequestDto();
        dto.setFromPlace("Минск");
        dto.setToPlace("Браслав");
        dto.setDepartureDate(LocalDateTime.now().plusDays(3));
        dto.setTotalSeats(4);
        dto.setDescription("Тестовая поездка");
        dto.setPaymentDetails("Карта: 1234");

        Trip trip = tripMapper.toEntity(dto);

        assertNotNull(trip);
        assertEquals("Минск", trip.getFromPlace());
        assertEquals("Браслав", trip.getToPlace());
        assertEquals(4, trip.getTotalSeats());
        assertEquals("Тестовая поездка", trip.getDescription());
        assertEquals("Карта: 1234", trip.getPaymentDetails());
    }
}
