package com.example.expense.repository;

import com.example.expense.model.Expense;
import com.example.expense.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUser(User user);
    Optional<Expense> findByIdAndUser(Long id, User user);
    
    @Query("SELECT e FROM Expense e WHERE e.user = :user AND e.expenseDate BETWEEN :startDate AND :endDate")
    List<Expense> findByUserAndDateRange(@Param("user") User user, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.status = 'PAID'")
    BigDecimal sumApprovedExpensesByUser(@Param("user") User user);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal sumExpensesByUserAndDateRange(@Param("user") User user, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(e.amount),0) FROM Expense e WHERE e.user = :user AND e.category = :category")
    BigDecimal sumExpensesByUserAndCategory(@Param("user") User user,
                                            @Param("category") com.example.expense.model.Category category);

    @Query("SELECT COALESCE(SUM(e.amount),0) FROM Expense e WHERE e.user = :user")
    BigDecimal sumAllExpensesByUser(@Param("user") User user);
}
