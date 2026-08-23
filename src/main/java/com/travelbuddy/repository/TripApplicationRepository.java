package com.travelbuddy.repository;


import com.travelbuddy.entity.TripApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripApplicationRepository extends JpaRepository<TripApplication, Long> {

    // НАХОДИТ ЗАЯВКУ ПО ID ПОЕЗДКИ И EMAIL ПАССАЖИРА
    Optional<TripApplication> findByTripIdAndPassengerEmail
    (Long tripId, String passengerEmail);

    // НАХОДИТ ВСЕ ЗАЯВКИ ПО ID ПОЕЗДКИ (нужно для завершения поездки)
    List<TripApplication> findByTripId(Long tripId);

    // ПРОВЕРЯЕТ, БЫЛА ЛИ У ПОЛЬЗОВАТЕЛЯ ЗАВЕРШЕННАЯ ПОЕЗДКА С ВОДИТЕЛЕМ
    Optional<TripApplication> findByPassengerIdAndTripDriverIdAndStatus
    (Long passengerId, Long driverId, String status);
}
