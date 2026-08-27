package com.travelbuddy.service;

import com.travelbuddy.dto.TripAdminDto;
import com.travelbuddy.dto.UserAdminDto;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private Trip testTrip;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("admin@buddy.by");
        testUser.setFullName("Admin");
        testUser.setRole("ROLE_ADMIN");

        testTrip = new Trip();
        testTrip.setId(1L);
        testTrip.setFromPlace("Минск");
        testTrip.setToPlace("Браслав");
        testTrip.setDepartureDate(LocalDateTime.now().plusDays(3));

        User driver = new User();
        driver.setEmail("driver@buddy.by");
        testTrip.setDriver(driver);
    }

    @Test
    void getAllUsers_ShouldReturnUserAdminDtos() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserAdminDto> dtos = adminService.getAllUsers();

        assertFalse(dtos.isEmpty());
        assertEquals(1, dtos.size());
        assertEquals("admin@buddy.by", dtos.get(0).getEmail());
        assertEquals("ROLE_ADMIN", dtos.get(0).getRole());
    }

    @Test
    void deleteUser_ShouldDelete_WhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        adminService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrow_WhenNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteUser(999L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllTrips_ShouldReturnTripAdminDtos() {
        when(tripRepository.findAll()).thenReturn(List.of(testTrip));

        List<TripAdminDto> dtos = adminService.getAllTrips();

        assertFalse(dtos.isEmpty());
        assertEquals(1, dtos.size());
        assertEquals("Минск", dtos.get(0).getFromPlace());
        assertEquals("driver@buddy.by", dtos.get(0).getDriverEmail());
    }

    @Test
    void deleteTrip_ShouldDelete_WhenExists() {
        when(tripRepository.existsById(1L)).thenReturn(true);

        adminService.deleteTrip(1L);

        verify(tripRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTrip_ShouldThrow_WhenNotFound() {
        when(tripRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> adminService.deleteTrip(999L));
        verify(tripRepository, never()).deleteById(anyLong());
    }
}
