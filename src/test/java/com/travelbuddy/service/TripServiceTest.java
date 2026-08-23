package com.travelbuddy.service;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripService tripService;

    private Trip testTrip;
    private User driver;

    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setEmail("driver@buddy.by");

        testTrip = new Trip();
        testTrip.setId(1L);
        testTrip.setTotalSeats(4);
        testTrip.setAvailableSeats(4);
        testTrip.setFromPlace("Минск");
        testTrip.setToPlace("Браслав");
    }

    @Test
    void getAllTrips_ShouldReturnList() {
        when(tripRepository.findAll()).thenReturn(List.of(testTrip));
        assertFalse(tripService.getAllTrips().isEmpty());
        assertEquals(1, tripService.getAllTrips().size());
    }

    @Test
    void createTrip_ShouldSave_WhenDriverExists() {
        when(userRepository.findByEmail("driver@buddy.by")).thenReturn(Optional.of(driver));
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

        Trip saved = tripService.createTrip(testTrip, "driver@buddy.by");
        assertNotNull(saved);
        assertEquals(driver, saved.getDriver());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    void createTrip_ShouldThrow_WhenDriverNotFound() {
        when(userRepository.findByEmail("unknown@buddy.by")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> tripService.createTrip(testTrip, "unknown@buddy.by"));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void findTripById_ShouldReturnTrip_WhenExists() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));

        Trip found = tripService.findTripById(1L);
        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void findTripById_ShouldThrow_WhenNotFound() {
        when(tripRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> tripService.findTripById(999L));
    }
}

