package com.travelbuddy.service;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripBudget;
import com.travelbuddy.repository.TripBudgetRepository;
import com.travelbuddy.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
}
