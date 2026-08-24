package com.travelbuddy.service;

import com.travelbuddy.dto.TripRequestDto;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.User;
import com.travelbuddy.mapper.TripMapper;
import com.travelbuddy.repository.TripApplicationRepository;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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

    @Mock
    private TripApplicationRepository tripApplicationRepository;

    @Mock
    private TripMapper tripMapper;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private TripService tripService;

    private Trip testTrip;
    private User driver;

    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setId(1L);
        driver.setEmail("driver@buddy.by");

        testTrip = new Trip();
        testTrip.setId(1L);
        testTrip.setTotalSeats(4);
        testTrip.setAvailableSeats(4);
        testTrip.setFromPlace("Минск");
        testTrip.setToPlace("Браслав");
        testTrip.setDriver(driver);
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

    @Test
    void createTripFromDto_ShouldSave_WhenDriverExists() {
        when(userRepository.findByEmail("driver@buddy.by")).thenReturn(Optional.of(driver));
        when(tripMapper.toEntity(any(TripRequestDto.class))).thenReturn(testTrip);
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

        TripRequestDto dto = new TripRequestDto();
        dto.setFromPlace("Минск");
        dto.setToPlace("Браслав");
        dto.setDepartureDate(LocalDateTime.now().plusDays(3));
        dto.setTotalSeats(4);
        dto.setDescription("Тест");
        dto.setPaymentDetails("Карта: 1234");

        Trip saved = tripService.createTripFromDto(dto, "driver@buddy.by");
        assertNotNull(saved);
        assertEquals(driver, saved.getDriver());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    void completeTrip_ShouldUpdateStatus_WhenDriver() {
        TripApplication app = new TripApplication();
        app.setStatus("ACCEPTED");

        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(tripApplicationRepository.findByTripId(1L)).thenReturn(List.of(app));
        when(tripApplicationRepository.save(any(TripApplication.class))).thenReturn(app);

        tripService.completeTrip(1L, "driver@buddy.by");
        assertEquals("COMPLETED", app.getStatus());
        verify(tripApplicationRepository, times(1)).save(app);
    }

    @Test
    void completeTrip_ShouldThrow_WhenNotDriver() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        assertThrows(RuntimeException.class,
                () -> tripService.completeTrip(1L, "other@buddy.by"));
        verify(tripApplicationRepository, never()).save(any(TripApplication.class));
    }

    @Test
    void getPaymentDetailsForParticipant_ShouldReturnDetails_WhenDriver() {
        testTrip.setPaymentDetails("Карта: 1234");
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(applicationService.isUserParticipant(1L, "driver@buddy.by")).thenReturn(true);

        String details = tripService.getPaymentDetailsForParticipant(1L, "driver@buddy.by");
        assertEquals("Карта: 1234", details);
    }

    @Test
    void getPaymentDetailsForParticipant_ShouldThrow_WhenNotParticipant() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(applicationService.isUserParticipant(1L, "other@buddy.by")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> tripService.getPaymentDetailsForParticipant(1L, "other@buddy.by"));
    }
}
