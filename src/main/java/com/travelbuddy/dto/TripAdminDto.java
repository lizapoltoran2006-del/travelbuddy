package com.travelbuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripAdminDto {
    private Long id;
    private String fromPlace;
    private String toPlace;
    private LocalDateTime departureDate;
    private String driverEmail;
}
