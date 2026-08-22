package com.travelbuddy.repository;

import com.travelbuddy.entity.BudgetPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetPaymentRepository extends JpaRepository<BudgetPayment, Long> {
    List<BudgetPayment> findByBudgetId(Long budgetId);
    Optional<BudgetPayment> findByBudgetIdAndUserId(Long budgetId, Long userId);
}
