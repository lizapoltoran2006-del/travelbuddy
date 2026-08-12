package com.travelbuddy.service;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.TripApplicationRepository;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private Trip testTrip;
    private User passenger;

    @BeforeEach
    void setUp() {
        passenger = new User();
        passenger.setEmail("pass@buddy.by");

        testTrip = new Trip();
        testTrip.setId(1L);
        testTrip.setAvailableSeats(5);
        testTrip.setTotalSeats(5);
    }

    @Test
    void applyForTrip_ShouldDecreaseSeats() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(userRepository.findByEmail("pass@buddy.by")).thenReturn(Optional.of(passenger));
        when(applicationRepository.save(any(TripApplication.class))).thenReturn(new TripApplication());

        applicationService.applyForTrip(1L, "pass@buddy.by", 2);
        assertEquals(3, testTrip.getAvailableSeats());
        verify(tripRepository, times(1)).save(testTrip);
    }

    @Test
    void applyForTrip_ShouldThrow_WhenNoSeats() {
        testTrip.setAvailableSeats(0);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(userRepository.findByEmail("pass@buddy.by")).thenReturn(Optional.of(passenger));

        assertThrows(RuntimeException.class, () -> applicationService.applyForTrip(1L, "pass@buddy.by", 1));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void applyForTrip_ShouldThrow_WhenSeatsInvalid() {
        assertThrows(RuntimeException.class, () -> applicationService.applyForTrip(1L, "pass@buddy.by", 0));
        assertThrows(RuntimeException.class, () -> applicationService.applyForTrip(1L, "pass@buddy.by", null));
        verify(tripRepository, never()).findById(anyLong());
    }

    @Test
    void cancelApplication_ShouldReturnSeats() {
        TripApplication app = new TripApplication();
        app.setTrip(testTrip);
        app.setPassenger(passenger);
        app.setStatus("ACCEPTED");
        app.setSeatsCount(3);
        testTrip.setAvailableSeats(1);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        applicationService.cancelApplication(1L, "pass@buddy.by");
        assertEquals(4, testTrip.getAvailableSeats());
        assertEquals("CANCELLED", app.getStatus());
        verify(tripRepository, times(1)).save(testTrip);
        verify(applicationRepository, times(1)).save(app);
    }

    @Test
    void cancelApplication_ShouldThrow_WhenNotOwner() {
        TripApplication app = new TripApplication();
        app.setPassenger(passenger);
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        assertThrows(RuntimeException.class, () -> applicationService.cancelApplication(1L, "another@buddy.by"));
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void cancelApplication_ShouldThrow_WhenAlreadyCancelled() {
        TripApplication app = new TripApplication();
        app.setPassenger(passenger);
        app.setStatus("CANCELLED");
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        assertThrows(RuntimeException.class, () -> applicationService.cancelApplication(1L, "pass@buddy.by"));
        verify(tripRepository, never()).save(any(Trip.class));
    }
}
