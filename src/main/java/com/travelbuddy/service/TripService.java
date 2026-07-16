package com.travelbuddy.service;


import com.travelbuddy.entity.Trip;
import com.travelbuddy.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }
}
