package com.travelbuddy.controller;


import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.User;
import com.travelbuddy.service.TripService;
import com.travelbuddy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;
    private final UserService userService;

    @GetMapping("/trips")
    public String showTripsPage(Model model) {
        List<Trip> trips = tripService.getAllTrips();
        model.addAttribute("tripsAttribute", trips);
        return "trips";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userAttribute", new  User());
        return "register";
    }


    @PostMapping("/register")
    public String processRegistration(@ModelAttribute ("userAttribute") User user) {
        userService.registerNewUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }
}
