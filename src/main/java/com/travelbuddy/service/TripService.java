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
        return applyForTrip(tripId, passengerEmail, 1);
    }

    @Transactional
    public TripApplication applyForTrip(Long tripId, String passengerEmail, Integer seatsCount) {

        if (seatsCount == null || seatsCount <= 0) {
            throw new RuntimeException("Количество мест должно быть больше 0");
        }

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));

        User passenger = userRepository.findByEmail(passengerEmail)
                .orElseThrow(() -> new RuntimeException("Пассажир не найден"));


        if (trip.getAvailableSeats() < seatsCount) {
            throw new RuntimeException("Недостаточно свободных мест. Доступно: " + trip.getAvailableSeats() + ", запрошено: " + seatsCount);
        }


        trip.setAvailableSeats(trip.getAvailableSeats() - seatsCount);
        tripRepository.save(trip);

        TripApplication application = new TripApplication();
        application.setTrip(trip);
        application.setPassenger(passenger);
        application.setStatus("ACCEPTED");
        application.setReactedAt(java.time.LocalDateTime.now());

        return tripApplicationRepository.save(application);
    }

    @Transactional
    public void cancelApplication(Long applicationId, String userEmail) {

        TripApplication application = tripApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));


        if (!application.getPassenger().getEmail().equals(userEmail)) {
            throw new RuntimeException("Вы можете отменить только свою заявку");
        }

        if (!"ACCEPTED".equals(application.getStatus())) {
            throw new RuntimeException("Эта заявка уже отменена");
        }

        Trip trip = application.getTrip();
        Integer seatsToReturn = application.getSeatsCount() != null ? application.getSeatsCount() : 1;
        trip.setAvailableSeats(trip.getAvailableSeats() + seatsToReturn);
        tripRepository.save(trip);


        application.setStatus("CANCELLED");
        tripApplicationRepository.save(application);
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

        return tripBudgetRepository.save(budget);
    }


}
