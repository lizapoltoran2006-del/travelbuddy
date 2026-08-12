package com.travelbuddy.controller;

import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.TripBudget;
import com.travelbuddy.service.ApplicationService;
import com.travelbuddy.service.BudgetService;
import com.travelbuddy.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final ApplicationService applicationService;  // ← инжектим
    private final BudgetService budgetService;            // ← инжектим

    //  Поездки

    @GetMapping("/api/trips")
    public List<Trip> getTrips() {
        return tripService.getAllTrips();
    }

    @PostMapping("/api/trips")
    public Trip createNewTrip(@RequestBody Trip trip, Principal principal) {
        String driverEmail = principal.getName();
        return tripService.createTrip(trip, driverEmail);
    }

    // Заявки

    @PostMapping("/api/trips/{id}/apply")
    public TripApplication applyForTrip(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Integer seats,
            Principal principal) {
        String passengerEmail = principal.getName();
        return applicationService.applyForTrip(id, passengerEmail, seats);
    }

    @DeleteMapping("/api/applications/{applicationId}")
    public String cancelApplication(@PathVariable Long applicationId, Principal principal) {
        String userEmail = principal.getName();
        applicationService.cancelApplication(applicationId, userEmail);
        return "Бронирование успешно отменено";
    }

    //  Бюджет

    @PostMapping("/api/trips/{id}/budget")
    public TripBudget addBudget(
            @PathVariable Long id,
            @RequestParam String expenseName,
            @RequestParam BigDecimal totalAmount) {
        return budgetService.calculateAndSaveBudget(id, expenseName, totalAmount);
    }

    @GetMapping("/api/trips/{id}/details")
    public Trip getTripDetails(@PathVariable Long id) {
        return tripService.findTripById(id);
    }
}


