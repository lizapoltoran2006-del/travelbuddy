package com.travelbuddy.service;

import com.travelbuddy.dto.BudgetPaymentDto;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripBudget;
import com.travelbuddy.entity.User;
import com.travelbuddy.entity.BudgetPayment;
import com.travelbuddy.repository.BudgetPaymentRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripBudgetRepository tripBudgetRepository;

    @Mock
    private BudgetPaymentRepository budgetPaymentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BudgetService budgetService;

    private Trip testTrip;

    @BeforeEach
    void setUp() {
        testTrip = new Trip();
        testTrip.setId(1L);
        testTrip.setTotalSeats(4);
    }

    @Test
    void calculateAndSaveBudget_ShouldCalculatePerPerson() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(tripBudgetRepository.save(any(TripBudget.class))).thenAnswer(inv -> inv.getArgument(0));

        TripBudget budget = budgetService.calculateAndSaveBudget(1L, "Бензин", BigDecimal.valueOf(100));

        assertNotNull(budget);
        assertEquals("Бензин", budget.getExpenseName());
        assertEquals(BigDecimal.valueOf(100), budget.getTotalAmount());
        assertEquals(0, BigDecimal.valueOf(25.00).compareTo(budget.getAmountPerPerson()));
        verify(tripBudgetRepository, times(1)).save(any(TripBudget.class));
    }

    @Test
    void calculateAndSaveBudget_ShouldThrow_WhenTripNotFound() {
        when(tripRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> budgetService.calculateAndSaveBudget(999L, "Бензин", BigDecimal.valueOf(100)));
        verify(tripBudgetRepository, never()).save(any(TripBudget.class));
    }

    @Test
    void calculateAndSaveBudget_ShouldHandleUnevenDivision() {
        testTrip.setTotalSeats(3);
        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(tripBudgetRepository.save(any(TripBudget.class))).thenAnswer(inv -> inv.getArgument(0));

        TripBudget budget = budgetService.calculateAndSaveBudget(1L, "Еда", BigDecimal.valueOf(100));

        assertEquals(0, BigDecimal.valueOf(33.33).compareTo(budget.getAmountPerPerson()));
    }


    // Тест для addBudget (только водитель)
    @Test
    void addBudget_ShouldSave_WhenDriver() {
        User driver = new User();
        driver.setEmail("driver@buddy.by");
        testTrip.setDriver(driver);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(tripBudgetRepository.save(any(TripBudget.class))).thenAnswer(inv -> inv.getArgument(0));

        TripBudget budget = budgetService.addBudget(1L, "Бензин", BigDecimal.valueOf(100), "driver@buddy.by");

        assertNotNull(budget);
        assertEquals("Бензин", budget.getExpenseName());
    }

    // Тест для addBudget — ошибка, если не водитель
    @Test
    void addBudget_ShouldThrow_WhenNotDriver() {
        User driver = new User();
        driver.setEmail("driver@buddy.by");
        testTrip.setDriver(driver);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));

        assertThrows(RuntimeException.class,
                () -> budgetService.addBudget(1L, "Бензин", BigDecimal.valueOf(100), "other@buddy.by"));
    }

    // Тест для markPayment
    @Test
    void markPayment_ShouldUpdateStatus_WhenValid() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@buddy.by");

        BudgetPayment payment = new BudgetPayment();
        payment.setId(1L);
        payment.setStatus("PENDING");
        payment.setUser(user);

        when(userRepository.findByEmail("user@buddy.by")).thenReturn(Optional.of(user));
        when(budgetPaymentRepository.findByBudgetIdAndUserId(1L, 1L)).thenReturn(Optional.of(payment));
        when(budgetPaymentRepository.save(any(BudgetPayment.class))).thenReturn(payment);

        BudgetPayment result = budgetService.markPayment(1L, "user@buddy.by");

        assertEquals("PAID", result.getStatus());
        assertNotNull(result.getPaidAt());
    }

    // Тест для markPayment — ошибка, если уже оплачено
    @Test
    void markPayment_ShouldThrow_WhenAlreadyPaid() {
        User user = new User();
        user.setId(1L);

        BudgetPayment payment = new BudgetPayment();
        payment.setStatus("PAID");
        payment.setUser(user);

        when(userRepository.findByEmail("user@buddy.by")).thenReturn(Optional.of(user));
        when(budgetPaymentRepository.findByBudgetIdAndUserId(1L, 1L)).thenReturn(Optional.of(payment));

        assertThrows(RuntimeException.class,
                () -> budgetService.markPayment(1L, "user@buddy.by"));
    }

    // Тест для getBudgetPaymentsDto
   /* @Test
    void getBudgetPaymentsDto_ShouldReturnDtoList() {
        TripBudget budget = new TripBudget();
        budget.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setEmail("user@buddy.by");

        BudgetPayment payment = new BudgetPayment();
        payment.setId(1L);
        payment.setUser(user);
        payment.setAmount(BigDecimal.valueOf(25));
        payment.setStatus("PENDING");

        when(tripRepository.findById(1L)).thenReturn(Optional.of(testTrip));
        when(tripBudgetRepository.findByTripId(1L)).thenReturn(Optional.of(budget));
        when(budgetPaymentRepository.findByBudgetId(1L)).thenReturn(List.of(payment));

        List<BudgetPaymentDto> dtos = budgetService.getBudgetPaymentsDto(1L);

        assertFalse(dtos.isEmpty());
        assertEquals(1L, dtos.get(0).getUserId());
        assertEquals("user@buddy.by", dtos.get(0).getUserEmail());
    }*/
}
