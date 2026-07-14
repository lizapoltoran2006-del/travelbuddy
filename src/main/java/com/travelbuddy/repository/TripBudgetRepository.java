package com.travelbuddy.repository;


import com.travelbuddy.entity.TripBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripBudgetRepository extends JpaRepository<TripBudget, Long> {
}
