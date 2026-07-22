package com.travelbuddy.controller;


import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.User;
import com.travelbuddy.service.TripService;
import com.travelbuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;
    private final UserService userService;
    private final com.travelbuddy.service.JwtService jwtService;

    @GetMapping("/trips")
    public String showTripsPage(Model model) {
        List<Trip> trips = tripService.getAllTrips();
        model.addAttribute("tripsAttribute", trips);
        return "trips";
    }

    // 1. REST-регистрация нового пользователя
    @PostMapping("/api/register")
    public com.travelbuddy.entity.User register(@org.springframework.web.bind.annotation.RequestBody com.travelbuddy.entity.User user) {

        return userService.registerNewUser(user);
    }

    // 2. REST-вход (Аутентификация) с выдачей JWT-токена
    @PostMapping("/api/login")
    public com.travelbuddy.dto.AuthResponse login(@org.springframework.web.bind.annotation.RequestBody com.travelbuddy.dto.AuthRequest request) {

        com.travelbuddy.entity.User user = userService.getUserRepository().findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Неверный email или пароль"));


        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return new com.travelbuddy.dto.AuthResponse(token);
    }

    // 3. REST-получение всех поездок по Беларуси
    @GetMapping("/api/trips")
    public java.util.List getTrips() {

        return tripService.getAllTrips();
    }



    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/api/trips")
    public Trip createNewTrip(
            @RequestBody Trip trip,
            Principal principal) {

        String driverEmail = principal.getName();
        return tripService.createTrip(trip, driverEmail);
    }
    @PostMapping("/api/trips/{id}/apply")
    public TripApplication applyForTrip(
            @PathVariable Long id,
            java.security.Principal principal) {
        String passengerEmail = principal.getName();
        return tripService.applyForTrip(id, passengerEmail);
    }

    @PostMapping("/api/trips/{id}/budget")
    public com.travelbuddy.entity.TripBudget addBudget(
            @PathVariable Long id,
            @RequestParam String expenseName,
            @RequestParam BigDecimal totalAmount) {

        return tripService.calculateAndSaveBudget(id, expenseName, totalAmount);
    }

}


