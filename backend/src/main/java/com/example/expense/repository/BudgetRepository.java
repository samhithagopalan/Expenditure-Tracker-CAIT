package com.example.expense.repository;

import com.example.expense.model.Budget;
import com.example.expense.model.Category;
import com.example.expense.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

    Optional<Budget> findByUserAndCategory(User user, Category category);
    long countByUser(User user);
}