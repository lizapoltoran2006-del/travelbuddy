package com.travelbuddy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budget_payments")
@Getter
@Setter
public class BudgetPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "budget_id", nullable = false)
    private TripBudget budget;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private BigDecimal amount;

    private LocalDateTime paidAt;

    private String status = "PENDING"; // PENDING, PAID, CANCELLED
}
