package com.travelbuddy.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

}
