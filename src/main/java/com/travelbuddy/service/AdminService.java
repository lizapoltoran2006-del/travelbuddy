package com.travelbuddy.service;

import com.travelbuddy.dto.TripAdminDto;
import com.travelbuddy.dto.UserAdminDto;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    public List<UserAdminDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Пользователь не найден");
        }
        userRepository.deleteById(userId);
    }

    public List<TripAdminDto> getAllTrips() {
        return tripRepository.findAll().stream()
                .map(this::mapToTripAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteTrip(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new RuntimeException("Поездка не найдена");
        }
        tripRepository.deleteById(tripId);
    }

    private UserAdminDto mapToUserAdminDto(User user) {
        return new UserAdminDto(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    private TripAdminDto mapToTripAdminDto(Trip trip) {
        return new TripAdminDto(
                trip.getId(),
                trip.getFromPlace(),
                trip.getToPlace(),
                trip.getDepartureDate(),
                trip.getDriver().getEmail()
        );
    }
}
