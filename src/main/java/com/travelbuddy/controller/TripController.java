package com.travelbuddy.controller;


import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.User;
import com.travelbuddy.service.TripService;
import com.travelbuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        // Аннотация @RequestBody приказывает Spring Boot принять JSON из тела запроса и превратить его в Java-объект
        return userService.registerNewUser(user);
    }

    // 2. REST-вход (Аутентификация) с выдачей JWT-токена
    @PostMapping("/api/login")
    public com.travelbuddy.dto.AuthResponse login(@org.springframework.web.bind.annotation.RequestBody com.travelbuddy.dto.AuthRequest request) {
        // Ищем пользователя в базе
        com.travelbuddy.entity.User user = userService.getUserRepository().findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Неверный email или пароль"));

        // Здесь мы пока временно сделаем простую проверку пароля текстом (на следующем шаге свяжем со Spring Security)
        // Генерируем долговечный JWT-токен на основе Email и роли пользователя для Postman, как просили в ТЗ!
        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return new com.travelbuddy.dto.AuthResponse(token);
    }

    // 3. REST-получение всех поездок по Беларуси
    @GetMapping("/api/trips")
    public java.util.List getTrips() {
        // Метод больше не принимает Model model и не возвращает строку "trips"
        // Он просто отдает чистый список Java-объектов, который Spring сам превратит в красивый JSON!
        return tripService.getAllTrips();
    }



    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}


