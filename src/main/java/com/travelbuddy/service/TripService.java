package com.travelbuddy.service;


import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.TripBudget;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.TripApplicationRepository;
import com.travelbuddy.repository.TripBudgetRepository;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripApplicationRepository tripApplicationRepository;
    private final TripBudgetRepository tripBudgetRepository;

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Trip createTrip(Trip trip, String driverEmail) {
        User driver = userRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new RuntimeException("Водитель не найден"));
        trip.setDriver(driver);
        trip.setAvailableSeats(trip.getTotalSeats());
        return tripRepository.save(trip);
    }

    @Transactional
    public TripApplication applyForTrip(Long tripId, String passengerEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));

        User passenger = userRepository.findByEmail(passengerEmail)
                .orElseThrow(() -> new RuntimeException("Пассажир не найден"));

        if (trip.getAvailableSeats() <= 0) {
            throw new RuntimeException("Извините, в этой поездке больше нет свободных мест");
        }

        trip.setAvailableSeats(trip.getAvailableSeats() - 1);
        tripRepository.save(trip);
        TripApplication application = new TripApplication();
        application.setTrip(trip);
        application.setPassenger(passenger);
        application.setStatus("ACCEPTED");
        application.setReactedAt(java.time.LocalDateTime.now());

        return tripApplicationRepository.save(application);
    }

    @Transactional
    public TripBudget calculateAndSaveBudget(Long tripId, String expenseName, BigDecimal totalAmount) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));


        BigDecimal totalSeats = BigDecimal.valueOf(trip.getTotalSeats());
        BigDecimal amountPerPerson = totalAmount.divide(totalSeats, 2, RoundingMode.HALF_UP);


        TripBudget budget = new TripBudget();
        budget.setTrip(trip);
        budget.setExpenseName(expenseName);
        budget.setTotalAmount(totalAmount);
        budget.setAmountPerPerson(amountPerPerson);

        // 4. Сохраняем в PostgreSQL
        return tripBudgetRepository.save(budget);
    }


}
