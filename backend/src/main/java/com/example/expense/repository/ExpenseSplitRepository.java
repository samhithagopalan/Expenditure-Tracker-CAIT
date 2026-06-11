package com.example.expense.repository;

import com.example.expense.model.Expense;
import com.example.expense.model.ExpenseSplit;
import com.example.expense.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    List<ExpenseSplit> findByExpense(Expense expense);
    List<ExpenseSplit> findByUser(User user);
    
    @Query("SELECT SUM(es.splitAmount) FROM ExpenseSplit es WHERE es.user = :user")
    BigDecimal sumSplitAmountsByUser(@Param("user") User user);
}
