package com.travelbuddy.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trip_applications")
@Getter
@Setter
public class TripApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String status;
    private java.time.LocalDateTime reactedAt; // когда попутчик нажал кнопку «Хочу поехать»

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "passenger_id")
    private User passenger;

}
