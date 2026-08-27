package com.travelbuddy.controller;

import com.travelbuddy.dto.BudgetPaymentDto;
import com.travelbuddy.dto.TripRequestDto;
import com.travelbuddy.entity.BudgetPayment;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.TripApplication;
import com.travelbuddy.entity.TripBudget;
import com.travelbuddy.repository.TripApplicationRepository;
import com.travelbuddy.service.ApplicationService;
import com.travelbuddy.service.BudgetService;
import com.travelbuddy.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final ApplicationService applicationService;
    private final BudgetService budgetService;


    //  Поездки

    @GetMapping("/api/trips")
    public List<Trip> getTrips() {
        return tripService.getAllTrips();
    }

    @PostMapping("/api/trips")
    public Trip createNewTrip(@Valid @RequestBody TripRequestDto tripDto, Principal principal) {
        return tripService.createTripFromDto(tripDto, principal.getName());
    }

    @PostMapping("/api/trips/{id}/complete")
    public String completeTrip(@PathVariable Long id, Principal principal) {
        tripService.completeTrip(id, principal.getName());
        return "Поездка завершена";
    }


    @GetMapping("/api/trips/{id}/payment")
    public ResponseEntity<String> getPaymentDetails(@PathVariable Long id, Principal principal) {
        String paymentDetails = tripService.getPaymentDetailsForParticipant(id, principal.getName());
        return ResponseEntity.ok(paymentDetails);
    }

    // Заявки

    @PostMapping("/api/trips/{id}/apply")
    public TripApplication applyForTrip(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Integer seats,
            Principal principal) {
        return applicationService.applyForTrip(id, principal.getName(), seats);
    }

    @DeleteMapping("/api/applications/{applicationId}")
    public String cancelApplication(@PathVariable Long applicationId, Principal principal) {
        applicationService.cancelApplication(applicationId, principal.getName());
        return "Бронирование успешно отменено";
    }

    //  Бюджет

    @PostMapping("/api/trips/{id}/budget")
    public TripBudget addBudget(
            @PathVariable Long id,
            @RequestParam String expenseName,
            @RequestParam BigDecimal totalAmount,
            Principal principal) {
        return budgetService.addBudget(id, expenseName, totalAmount, principal.getName());
    }

    @GetMapping("/api/trips/{id}/details")
    public Trip getTripDetails(@PathVariable Long id) {
        return tripService.findTripById(id);
    }

    // Получение бюджета с платежами
    @GetMapping("/api/trips/{id}/budget")
    public List<TripBudget> getBudget(@PathVariable Long id) {
        return budgetService.getBudgetsByTrip(id);
    }
    // Отметка оплаты
    @PostMapping("/api/budgets/{budgetId}/pay")
    public String payBudget(@PathVariable Long budgetId, Principal principal) {
        budgetService.markPayment(budgetId, principal.getName());
        return "Платёж отмечен как оплаченный";
    }

    @GetMapping("/api/trips/{id}/budget/payments")
    public List<BudgetPaymentDto> getPayments(@PathVariable Long id) {
        return budgetService.getBudgetPaymentsDto(id);
    }
}



