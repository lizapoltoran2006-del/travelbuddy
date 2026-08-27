package com.travelbuddy.mapper;

import com.travelbuddy.dto.TripRequestDto;
import com.travelbuddy.entity.Trip;
import org.springframework.stereotype.Component;

@Component
public class TripMapper {

    public Trip toEntity(TripRequestDto dto) {
        Trip trip = new Trip();
        trip.setFromPlace(dto.getFromPlace());
        trip.setToPlace(dto.getToPlace());
        trip.setDepartureDate(dto.getDepartureDate());
        trip.setTotalSeats(dto.getTotalSeats());
        trip.setDescription(dto.getDescription());
        trip.setPaymentDetails(dto.getPaymentDetails());
        return trip;
    }
}