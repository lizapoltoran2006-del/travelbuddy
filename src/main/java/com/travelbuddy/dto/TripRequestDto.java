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
public class TripRequestDto {
    private String fromPlace;
    private String toPlace;
    private LocalDateTime departureDate;
    private Integer totalSeats;
    private String description;
    private String paymentDetails; // Реквизиты карты/телефона для оплаты
}