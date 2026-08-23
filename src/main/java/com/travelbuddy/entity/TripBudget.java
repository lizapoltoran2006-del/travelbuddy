package com.travelbuddy.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trip_budgets")
@Getter
@Setter
public class TripBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String expenseName; // название расхода
    private java.math.BigDecimal totalAmount; // вся сумма
    private java.math.BigDecimal amountPerPerson; // сколько должен скинуться каждый

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BudgetPayment> payments = new ArrayList<>();

}
