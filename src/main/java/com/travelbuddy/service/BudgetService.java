package com.travelbuddy.service;

import com.travelbuddy.dto.BudgetPaymentDto;
import com.travelbuddy.entity.BudgetPayment;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripBudget;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final TripRepository tripRepository;
    private final TripBudgetRepository tripBudgetRepository;
    private final BudgetPaymentRepository budgetPaymentRepository;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;

    @Transactional
    public TripBudget calculateAndSaveBudget(Long tripId, String expenseName, BigDecimal totalAmount) {
        // 1. Находим поездку
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));


        int participantsCount = applicationService.getActualParticipantsCount(tripId);
        BigDecimal totalParticipants = BigDecimal.valueOf(participantsCount);

        BigDecimal amountPerPerson = totalAmount.divide(totalParticipants, 2, RoundingMode.HALF_UP);

        // 3. Создаем объект бюджета
        TripBudget budget = new TripBudget();
        budget.setTrip(trip);
        budget.setExpenseName(expenseName);
        budget.setTotalAmount(totalAmount);
        budget.setAmountPerPerson(amountPerPerson);

        TripBudget savedBudget = tripBudgetRepository.save(budget);

        createPaymentsForBudget(savedBudget, trip);

        return savedBudget;
    }

    private void createPaymentsForBudget(TripBudget budget, Trip trip) {
        // Получаем всех участников поездки (водитель + пассажиры)
        List<User> participants = userRepository.findParticipantsByTripId(trip.getId());

        for (User participant : participants) {
            BudgetPayment payment = new BudgetPayment();
            payment.setBudget(budget);
            payment.setUser(participant);
            payment.setAmount(budget.getAmountPerPerson());
            payment.setStatus("PENDING");
            budgetPaymentRepository.save(payment);
        }
}

    public TripBudget getBudgetById(Long budgetId) {
        return tripBudgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Бюджет не найден"));
    }

    // Отметка оплаты
    @Transactional
    public BudgetPayment markPayment(Long budgetId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        BudgetPayment payment = budgetPaymentRepository.findByBudgetIdAndUserId(budgetId, user.getId())
                .orElseThrow(() -> new RuntimeException("Платёж не найден для этого пользователя"));

        if ("PAID".equals(payment.getStatus())) {
            throw new RuntimeException("Этот платёж уже оплачен");
        }

        payment.setStatus("PAID");
        payment.setPaidAt(LocalDateTime.now());

        return budgetPaymentRepository.save(payment);
    }



    public List<BudgetPaymentDto> getBudgetPaymentsDto(Long tripId) {
        // 1. Получаем все бюджеты поездки
        List<TripBudget> budgets = tripBudgetRepository.findByTripId(tripId);

        if (budgets.isEmpty()) {
            return Collections.emptyList(); // Если бюджетов нет — возвращаем пустой список
        }

        List<BudgetPayment> allPayments = new ArrayList<>();
        for (TripBudget budget : budgets) {
            allPayments.addAll(budgetPaymentRepository.findByBudgetId(budget.getId()));
        }

        return allPayments.stream().map(payment -> {
            BudgetPaymentDto dto = new BudgetPaymentDto();
            dto.setId(payment.getId());
            dto.setUserId(payment.getUser().getId());
            dto.setUserEmail(payment.getUser().getEmail());
            dto.setAmount(payment.getAmount());
            dto.setStatus(payment.getStatus());
            dto.setPaidAt(payment.getPaidAt());
            return dto;
        }).collect(Collectors.toList());
    }
    @Transactional
    public TripBudget addBudget(Long tripId, String expenseName, BigDecimal totalAmount, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));

        if (!trip.getDriver().getEmail().equals(userEmail)) {
            throw new RuntimeException("Только водитель может добавлять бюджет");
        }

        return calculateAndSaveBudget(tripId, expenseName, totalAmount);
    }

    public List<TripBudget> getBudgetsByTrip(Long tripId) {
        // Проверяем, что поездка существует
        tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));

        // Возвращаем все бюджеты для этой поездки
        return tripBudgetRepository.findByTripId(tripId);
    }
}
