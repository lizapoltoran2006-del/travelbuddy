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
    private final TripApplicationRepository tripApplicationRepository;


    //  Поездки

    @GetMapping("/api/trips")
    public List<Trip> getTrips() {
        return tripService.getAllTrips();
    }

    @PostMapping("/api/trips")
    public Trip createNewTrip(@RequestBody TripRequestDto tripDto, Principal principal) {
        String driverEmail = principal.getName();
        Trip trip = new Trip();
        trip.setFromPlace(tripDto.getFromPlace());
        trip.setToPlace(tripDto.getToPlace());
        trip.setDepartureDate(tripDto.getDepartureDate());
        trip.setTotalSeats(tripDto.getTotalSeats());
        trip.setDescription(tripDto.getDescription());
        trip.setPaymentDetails(tripDto.getPaymentDetails());
        return tripService.createTrip(trip, driverEmail);

    }
    @PostMapping("/api/trips/{id}/complete")
    public String completeTrip(@PathVariable Long id, Principal principal) {
        Trip trip = tripService.findTripById(id);
        if (!trip.getDriver().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Только водитель может завершить поездку");
        }
        List<TripApplication> applications = tripApplicationRepository.findByTripId(id);
        for (TripApplication app : applications) {
            app.setStatus("COMPLETED");
            tripApplicationRepository.save(app);
        }
        return "Поездка завершена";
    }

    @GetMapping("/api/trips/{id}/payment")
    public ResponseEntity<String> getPaymentDetails(@PathVariable Long id, Principal principal) {
        Trip trip = tripService.findTripById(id);
        if (!applicationService.isUserParticipant(id, principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Доступ запрещён: вы не участник этой поездки");
        }
        return ResponseEntity.ok(trip.getPaymentDetails());
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
            @RequestParam BigDecimal totalAmount,
            Principal principal) {
        Trip trip = tripService.findTripById(id);
        if (!trip.getDriver().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Только водитель может добавлять бюджет");
        }
        return budgetService.calculateAndSaveBudget(id, expenseName, totalAmount);
    }

    @GetMapping("/api/trips/{id}/details")
    public Trip getTripDetails(@PathVariable Long id) {
        return tripService.findTripById(id);
    }

    // Получение бюджета с платежами
    @GetMapping("/api/trips/{id}/budget")
    public TripBudget getBudget(@PathVariable Long id) {
        return budgetService.getBudgetWithPayments(id);
    }

    // Отметка оплаты
    @PostMapping("/api/trips/{id}/budget/pay")
    public String payBudget(@PathVariable Long id, Principal principal) {
        TripBudget budget = budgetService.getBudgetWithPayments(id);
        budgetService.markPayment(budget.getId(), principal.getName());
        return "Платёж отмечен как оплаченный";
    }

    @GetMapping("/api/trips/{id}/budget/payments")
    public List<BudgetPaymentDto> getPayments(@PathVariable Long id) {
        return budgetService.getBudgetPaymentsDto(id);
    }
}



