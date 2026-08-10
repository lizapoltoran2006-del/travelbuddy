package com.travelbuddy.service;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.TripBudget;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.TripApplicationRepository;
import com.travelbuddy.repository.TripBudgetRepository;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TripApplicationRepository tripApplicationRepository;

    @Mock
    private TripBudgetRepository tripBudgetRepository;

    @InjectMocks
    private TripService tripService;

    private User testDriver;
    private User testPassenger;
    private Trip testTrip;

    @BeforeEach
    void setUp() {
        testDriver = new User();
        testDriver.setId(1L);
        testDriver.setEmail("driver@buddy.by");
        testDriver.setFullName("Driver");

        testPassenger = new User();
        testPassenger.setId(2L);
        testPassenger.setEmail("passenger@buddy.by");
        testPassenger.setFullName("Passenger");

        testTrip = new Trip();
        testTrip.setId(1L);
        testTrip.setFromPlace("Минск");
        testTrip.setToPlace("Браслав");
        testTrip.setDepartureDate(LocalDateTime.now().plusDays(3));
        testTrip.setTotalSeats(4);
        testTrip.setAvailableSeats(4);
        testTrip.setDescription("Тестовая поездка");
        testTrip.setDriver(testDriver);
    }

    @Test
    void getAllTrips_ShouldReturnListOfTrips() {
        when(tripRepository.findAll()).thenReturn(List.of(testTrip));

        List<Trip> trips = tripService.getAllTrips();

        assertFalse(trips.isEmpty());
        assertEquals(1, trips.size());
        assertEquals("Минск", trips.get(0).getFromPlace());
        verify(tripRepository, times(1)).findAll();
    }

    @Test
    void createTrip_ShouldSaveTrip_WhenDriverExists() {
        when(userRepository.findByEmail("driver@buddy.by")).thenReturn(Optional.of(testDriver));
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

        Trip savedTrip = tripService.createTrip(testTrip, "driver@buddy.by");

        assertNotNull(savedTrip);
        assertEquals(testDriver, savedTrip.getDriver());
        assertEquals(4, savedTrip.getAvailableSeats());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    void createTrip_ShouldThrowException_WhenDriverNotFound() {
        when(userRepository.findByEmail("unknown@buddy.by")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.createTrip(testTrip, "unknown@buddy.by"));
        assertEquals("Водитель не найден", exception.getMessage());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void applyForTrip_ShouldDecreaseAvailableSeats_WhenSeatsAvailable() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(userRepository.findByEmail("passenger@buddy.by")).thenReturn(Optional.of(testPassenger));
        when(tripApplicationRepository.save(any(TripApplication.class))).thenReturn(new TripApplication());

        TripApplication application = tripService.applyForTrip(1L, "passenger@buddy.by");

        assertNotNull(application);
        assertEquals(3, testTrip.getAvailableSeats());
        verify(tripRepository, times(1)).save(testTrip);
        verify(tripApplicationRepository, times(1)).save(any(TripApplication.class));
    }

    @Test
    void applyForTrip_ShouldThrowException_WhenNoSeatsAvailable() {
        testTrip.setAvailableSeats(0);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(userRepository.findByEmail("passenger@buddy.by")).thenReturn(Optional.of(testPassenger));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.applyForTrip(1L, "passenger@buddy.by"));

        assertEquals("Недостаточно свободных мест. Доступно: 0, запрошено: 1", exception.getMessage());
        verify(tripRepository, never()).save(any(Trip.class));

    }

    @Test
    void applyForTrip_ShouldThrowException_WhenTripNotFound() {
        when(tripRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.applyForTrip(999L, "passenger@buddy.by"));
        assertEquals("Поездка не найдена", exception.getMessage());
    }

    @Test
    void applyForTrip_ShouldThrowException_WhenPassengerNotFound() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(userRepository.findByEmail("unknown@buddy.by")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.applyForTrip(1L, "unknown@buddy.by"));
        assertEquals("Пассажир не найден", exception.getMessage());
    }

    @Test
    void calculateAndSaveBudget_ShouldCalculateAmountPerPerson() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(tripBudgetRepository.save(any(TripBudget.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TripBudget budget = tripService.calculateAndSaveBudget(1L, "Бензин", BigDecimal.valueOf(100));

        assertNotNull(budget);
        assertEquals("Бензин", budget.getExpenseName());
        assertEquals(BigDecimal.valueOf(100), budget.getTotalAmount());
        // Исправлено: используем compareTo вместо equals
        assertEquals(0, BigDecimal.valueOf(25.00).compareTo(budget.getAmountPerPerson()));
        verify(tripBudgetRepository, times(1)).save(any(TripBudget.class));
    }


    @Test
    void calculateAndSaveBudget_ShouldThrowException_WhenTripNotFound() {
        when(tripRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.calculateAndSaveBudget(999L, "Бензин", BigDecimal.valueOf(100)));
        assertEquals("Поездка не найдена", exception.getMessage());
        verify(tripBudgetRepository, never()).save(any(TripBudget.class));
    }

    @Test
    void createTrip_ShouldSetAvailableSeats_EqualToTotalSeats() {
        when(userRepository.findByEmail("driver@buddy.by")).thenReturn(Optional.of(testDriver));
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);

        Trip savedTrip = tripService.createTrip(testTrip, "driver@buddy.by");

        assertNotNull(savedTrip);
        assertEquals(testTrip.getTotalSeats(), savedTrip.getAvailableSeats());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }


    @Test
    void applyForTrip_ShouldSetStatusAndReactedAt() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(userRepository.findByEmail("passenger@buddy.by")).thenReturn(Optional.of(testPassenger));
        when(tripApplicationRepository.save(any(TripApplication.class))).thenAnswer(invocation -> {
            TripApplication saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        TripApplication application = tripService.applyForTrip(1L, "passenger@buddy.by");

        assertNotNull(application);
        assertEquals("ACCEPTED", application.getStatus());
        assertNotNull(application.getReactedAt());
        verify(tripApplicationRepository, times(1)).save(any(TripApplication.class));
    }

    @Test
    void getAllTrips_ShouldReturnEmptyList_WhenNoTrips() {
        when(tripRepository.findAll()).thenReturn(List.of());

        List<Trip> trips = tripService.getAllTrips();

        assertNotNull(trips);
        assertTrue(trips.isEmpty());
    }


    @Test
    void applyForTrip_WithMultipleSeats_ShouldBookMultipleSeats() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(userRepository.findByEmail("passenger@buddy.by")).thenReturn(Optional.of(testPassenger));
        when(tripApplicationRepository.save(any(TripApplication.class))).thenReturn(new TripApplication());

        TripApplication application = tripService.applyForTrip(1L, "passenger@buddy.by", 3);

        assertNotNull(application);
        assertEquals(1, testTrip.getAvailableSeats());
        verify(tripRepository, times(1)).save(testTrip);
    }

    @Test
    void applyForTrip_WithSeatsCountGreaterThanAvailable_ShouldThrowException() {
        testTrip.setAvailableSeats(2);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(userRepository.findByEmail("passenger@buddy.by")).thenReturn(Optional.of(testPassenger));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.applyForTrip(1L, "passenger@buddy.by", 5));
        assertEquals("Недостаточно свободных мест. Доступно: 2, запрошено: 5", exception.getMessage());
        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void applyForTrip_WithZeroSeats_ShouldThrowException() {

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.applyForTrip(1L, "passenger@buddy.by", 0));
        assertEquals("Количество мест должно быть больше 0", exception.getMessage());

        verify(tripRepository, never()).findById(anyLong());
        verify(userRepository, never()).findByEmail(anyString());
        verify(tripApplicationRepository, never()).save(any(TripApplication.class));
    }

    @Test
    void applyForTrip_WithNullSeats_ShouldThrowException() {

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.applyForTrip(1L, "passenger@buddy.by", null));
        assertEquals("Количество мест должно быть больше 0", exception.getMessage());

        verify(tripRepository, never()).findById(anyLong());
        verify(userRepository, never()).findByEmail(anyString());
        verify(tripApplicationRepository, never()).save(any(TripApplication.class));
    }

    @Test
    void cancelApplication_ShouldReturnSeats_WhenValid() {
        // создаем заявку с 3 местами
        TripApplication application = new TripApplication();
        application.setId(1L);
        application.setTrip(testTrip);
        application.setPassenger(testPassenger);
        application.setStatus("ACCEPTED");
        application.setSeatsCount(3);

        testTrip.setAvailableSeats(1); // было 4 забронировали 3 → осталось 1

        when(tripApplicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(tripRepository.save(any(Trip.class))).thenReturn(testTrip);
        when(tripApplicationRepository.save(any(TripApplication.class))).thenReturn(application);

        //  отменяем бронирование
        tripService.cancelApplication(1L, "passenger@buddy.by");

        // Проверка: места вернулись
        assertEquals(4, testTrip.getAvailableSeats()); // 1 + 3 = 4
        assertEquals("CANCELLED", application.getStatus());
        verify(tripRepository, times(1)).save(testTrip);
        verify(tripApplicationRepository, times(1)).save(application);
    }

    @Test
    void cancelApplication_ShouldThrowException_WhenApplicationNotFound() {
        when(tripApplicationRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.cancelApplication(999L, "passenger@buddy.by"));
        assertEquals("Заявка не найдена", exception.getMessage());
    }

    @Test
    void cancelApplication_ShouldThrowException_WhenUserIsNotOwner() {
        TripApplication application = new TripApplication();
        application.setId(1L);
        application.setPassenger(testPassenger);

        when(tripApplicationRepository.findById(1L)).thenReturn(Optional.of(application));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.cancelApplication(1L, "another@buddy.by"));
        assertEquals("Вы можете отменить только свою заявку", exception.getMessage());
    }

    @Test
    void cancelApplication_ShouldThrowException_WhenAlreadyCancelled() {
        TripApplication application = new TripApplication();
        application.setId(1L);
        application.setPassenger(testPassenger);
        application.setStatus("CANCELLED");

        when(tripApplicationRepository.findById(1L)).thenReturn(Optional.of(application));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tripService.cancelApplication(1L, "passenger@buddy.by"));
        assertEquals("Эта заявка уже отменена", exception.getMessage());
    }


}

