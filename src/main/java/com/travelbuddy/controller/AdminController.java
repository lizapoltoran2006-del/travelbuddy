package com.travelbuddy.controller;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("Пользователь удалён");
    }

    @GetMapping("/trips")
    public ResponseEntity<List<Trip>> getAllTrips() {
        return ResponseEntity.ok(tripRepository.findAll());
    }

    @DeleteMapping("/trips/{id}")
    public ResponseEntity<String> deleteTrip(@PathVariable Long id) {
        if (!tripRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Поездка не найдена");
        }
        tripRepository.deleteById(id);
        return ResponseEntity.ok("Поездка удалена");
    }
}
