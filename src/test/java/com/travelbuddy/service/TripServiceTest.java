package com.travelbuddy.service;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.repository.TripRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private TripService tripService;

    @Test
    public void getAllTrips_Success() {
        // Given
        Trip trip = new Trip();
        trip.setFromPlace("Минск");
        trip.setToPlace("Браслав");

        Mockito.when(tripRepository.findAll()).thenReturn(List.of(trip));

        // When
        List<Trip> trips = tripService.getAllTrips();

        // Then
        org.junit.jupiter.api.Assertions.assertFalse(trips.isEmpty());
        org.junit.jupiter.api.Assertions.assertEquals(1, trips.size());
        org.junit.jupiter.api.Assertions.assertEquals("Минск", trips.get(0).getFromPlace());
    }
}

