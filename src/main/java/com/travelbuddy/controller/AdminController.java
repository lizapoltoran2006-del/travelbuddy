package com.travelbuddy.controller;

import com.travelbuddy.dto.TripAdminDto;
import com.travelbuddy.dto.UserAdminDto;
import com.travelbuddy.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserAdminDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("Пользователь удалён");
    }

    @GetMapping("/trips")
    public ResponseEntity<List<TripAdminDto>> getAllTrips() {
        return ResponseEntity.ok(adminService.getAllTrips());
    }

    @DeleteMapping("/trips/{id}")
    public ResponseEntity<String> deleteTrip(@PathVariable Long id) {
        adminService.deleteTrip(id);
        return ResponseEntity.ok("Поездка удалена");
    }

}
