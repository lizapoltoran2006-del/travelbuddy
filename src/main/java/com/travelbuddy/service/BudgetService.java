package com.travelbuddy.service;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripBudget;
import com.travelbuddy.repository.TripBudgetRepository;
import com.travelbuddy.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final TripRepository tripRepository;
    private final TripBudgetRepository tripBudgetRepository;

    @Transactional
    public TripBudget calculateAndSaveBudget(Long tripId, String expenseName, BigDecimal totalAmount) {
        // 1. Находим поездку
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));

        // 2. Рассчитываем сумму на человека
        // totalSeats — общее количество мест в машине (не свободных!)
        BigDecimal totalSeats = BigDecimal.valueOf(trip.getTotalSeats());

        // Делим общую сумму на количество мест, округляем до 2 знаков
        BigDecimal amountPerPerson = totalAmount.divide(totalSeats, 2, RoundingMode.HALF_UP);

        // 3. Создаем объект бюджета
        TripBudget budget = new TripBudget();
        budget.setTrip(trip);
        budget.setExpenseName(expenseName);
        budget.setTotalAmount(totalAmount);
        budget.setAmountPerPerson(amountPerPerson);

        // 4. Сохраняем в БД
        return tripBudgetRepository.save(budget);
    }
}
