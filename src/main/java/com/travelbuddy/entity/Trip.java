package com.travelbuddy.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trips")
@Getter
@Setter
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fromPlace;
    private String toPlace;
    private java.time.LocalDateTime departureDate; // дата и время выезда
    private Integer totalSeats; // всего мест в машине
    private Integer availableSeats; // сколько свободных мест осталось
    private String description; // описание поездки, пожелания водителя
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;

}
