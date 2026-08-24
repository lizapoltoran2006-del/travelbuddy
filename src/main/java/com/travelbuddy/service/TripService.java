package com.travelbuddy.service;

import com.travelbuddy.dto.TripRequestDto;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.User;
import com.travelbuddy.mapper.TripMapper;
import com.travelbuddy.repository.TripApplicationRepository;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripMapper tripMapper;
    private final TripApplicationRepository tripApplicationRepository;
    private final ApplicationService applicationService;

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

    public Trip findTripById(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));
    }
    public Trip createTripFromDto(TripRequestDto tripDto, String driverEmail) {
        User driver = userRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new RuntimeException("Водитель не найден"));

        Trip trip = tripMapper.toEntity(tripDto); // маппер
        trip.setDriver(driver);
        trip.setAvailableSeats(trip.getTotalSeats());

        return tripRepository.save(trip);
    }

    @Transactional
    public void completeTrip(Long tripId, String userEmail) {
        Trip trip = findTripById(tripId);

        if (!trip.getDriver().getEmail().equals(userEmail)) {
            throw new RuntimeException("Только водитель может завершить поездку");
        }

        List<TripApplication> applications = tripApplicationRepository.findByTripId(tripId);
        for (TripApplication app : applications) {
            app.setStatus("COMPLETED");
            tripApplicationRepository.save(app);
        }
    }

    public String getPaymentDetailsForParticipant(Long tripId, String userEmail) {
        // 1. Находим поездку
        Trip trip = findTripById(tripId);

        // 2. Проверяем, что пользователь — участник поездки
        if (!applicationService.isUserParticipant(tripId, userEmail)) {
            throw new RuntimeException("Доступ запрещён: вы не участник этой поездки");
        }

        // 3. Возвращаем реквизиты оплаты
        return trip.getPaymentDetails();
    }

}
