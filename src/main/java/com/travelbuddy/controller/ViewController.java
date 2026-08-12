package com.travelbuddy.controller;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.User;
import com.travelbuddy.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller  // ← Возвращает HTML, а не JSON!
@RequiredArgsConstructor
public class ViewController {

    private final TripService tripService;

    @GetMapping("/trips")
    public String showTripsPage(Model model) {
        List<Trip> trips = tripService.getAllTrips();
        model.addAttribute("tripsAttribute", trips);
        return "trips"; // → trips.html
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // → login.html
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userAttribute", new User());
        return "register"; // → register.html
    }

    // Обработка регистрации через форму (не REST)
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("userAttribute") User user) {
        // Здесь нужен отдельный метод в UserService для регистрации через форму
        // return "redirect:/login";
        return "redirect:/login";
    }
}
