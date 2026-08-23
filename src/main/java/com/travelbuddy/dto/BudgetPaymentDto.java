package com.travelbuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetPaymentDto {
    private Long id;
    private Long userId;
    private String userEmail;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidAt;
}
