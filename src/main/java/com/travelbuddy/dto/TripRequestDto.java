package com.travelbuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripRequestDto {

    @NotBlank(message = "Откуда не может быть пустым")
    private String fromPlace;

    @NotBlank(message = "Куда не может быть пустым")
    private String toPlace;

    @NotNull(message = "Дата отправления обязательна")
    @Future(message = "Дата должна быть в будущем")
    private LocalDateTime departureDate;

    @NotNull(message = "Количество мест обязательно")
    @Min(value = 1, message = "Минимум 1 место")
    private Integer totalSeats;

    @Size(max = 500, message = "Описание не длиннее 500 символов")
    private String description;

    @Size(max = 200, message = "Реквизиты оплаты не должны превышать 200 символов")
    private String paymentDetails;
}