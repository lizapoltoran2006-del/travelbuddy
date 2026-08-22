package com.travelbuddy.repository;

import com.travelbuddy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    java.util.Optional<User> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u " +
            "LEFT JOIN TripApplication ta ON ta.passenger = u " +
            "WHERE ta.trip.id = :tripId AND (ta.status = 'ACCEPTED' OR ta.status = 'COMPLETED') " +
            "UNION " +
            "SELECT u FROM User u JOIN Trip t ON t.driver = u WHERE t.id = :tripId")
    List<User> findParticipantsByTripId(@Param("tripId") Long tripId);
}
