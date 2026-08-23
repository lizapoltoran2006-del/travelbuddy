package com.travelbuddy.service;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.TripApplicationRepository;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripApplicationRepository tripApplicationRepository;

    @Transactional
    public TripApplication applyForTrip(Long tripId, String passengerEmail, Integer seatsCount) {
        // 1. Проверяем количество мест
        if (seatsCount == null || seatsCount <= 0) {
            throw new RuntimeException("Количество мест должно быть больше 0");
        }

        // 2. Находим поездку
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));

        // 3. Находим пассажира
        User passenger = userRepository.findByEmail(passengerEmail)
                .orElseThrow(() -> new RuntimeException("Пассажир не найден"));

        // 4. Проверяем, есть ли свободные места
        if (trip.getAvailableSeats() < seatsCount) {
            throw new RuntimeException("Недостаточно свободных мест. Доступно: " +
                    trip.getAvailableSeats() + ", запрошено: " + seatsCount);
        }

        // 5. Уменьшаем количество доступных мест
        trip.setAvailableSeats(trip.getAvailableSeats() - seatsCount);
        tripRepository.save(trip);

        // 6. Создаем заявку
        TripApplication application = new TripApplication();
        application.setTrip(trip);
        application.setPassenger(passenger);
        application.setStatus("ACCEPTED");
        application.setReactedAt(LocalDateTime.now());
        application.setSeatsCount(seatsCount);

        return tripApplicationRepository.save(application);
    }

    @Transactional
    public void cancelApplication(Long applicationId, String userEmail) {
        // 1. Находим заявку
        TripApplication application = tripApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Заявка не найдена"));

        // 2. Проверяем, что отменяет тот, кто подавал
        if (!application.getPassenger().getEmail().equals(userEmail)) {
            throw new RuntimeException("Вы можете отменить только свою заявку");
        }

        // 3. Проверяем, что заявка еще активна
        if (!"ACCEPTED".equals(application.getStatus())) {
            throw new RuntimeException("Эта заявка уже отменена");
        }

        // 4. Возвращаем места обратно
        Trip trip = application.getTrip();
        Integer seatsToReturn = application.getSeatsCount() != null ? application.getSeatsCount() : 1;
        trip.setAvailableSeats(trip.getAvailableSeats() + seatsToReturn);
        tripRepository.save(trip);

        // 5. Меняем статус заявки
        application.setStatus("CANCELLED");
        tripApplicationRepository.save(application);
    }

    //  Проверка, участвует ли пользователь в поездке
    public boolean isUserParticipant(Long tripId, String userEmail) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));

        // Если пользователь — водитель
        if (trip.getDriver().getEmail().equals(userEmail)) {
            return true;
        }

        // Если пользователь — пассажир с активной заявкой
        return tripApplicationRepository
                .findByTripIdAndPassengerEmail(tripId, userEmail)
                .map(app -> "ACCEPTED".equals(app.getStatus()) || "COMPLETED".equals(app.getStatus()))
                .orElse(false);
    }

    //  Проверка, была ли завершенная поездка между пассажиром и водителем
    public boolean hasCompletedTripWithDriver(Long passengerId, Long driverId) {
        return tripApplicationRepository
                .findByPassengerIdAndTripDriverIdAndStatus(passengerId, driverId, "COMPLETED")
                .isPresent();
    }
}
