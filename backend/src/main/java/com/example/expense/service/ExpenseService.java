package com.example.expense.service;

import com.example.expense.model.Expense;
import com.example.expense.model.User;
import com.example.expense.model.Category;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.UserRepository;
import com.example.expense.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Expense createExpense(Long userId, Long categoryId, String description, BigDecimal amount, LocalDateTime expenseDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }

        Expense expense = Expense.builder()
                .user(user)
                .category(category)
                .description(description)
                .amount(amount)
                .expenseDate(expenseDate != null ? expenseDate : LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return expenseRepository.save(expense);
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public List<Expense> getExpensesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByUser(user);
    }

    public List<Expense> getExpensesByUserAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseRepository.findByUserAndDateRange(user, startDate, endDate);
    }

    public BigDecimal getTotalApprovedExpensesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BigDecimal total = expenseRepository.sumApprovedExpensesByUser(user);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpensesByUserInDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BigDecimal total = expenseRepository.sumExpensesByUserAndDateRange(user, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public Expense updateExpense(Long id, Long userId, String description, BigDecimal amount, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Expense not found for this user"));

        if (description != null) expense.setDescription(description);
        if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) expense.setAmount(amount);
        if (status != null) expense.setStatus(com.example.expense.model.ExpenseStatus.valueOf(status));
        expense.setUpdatedAt(LocalDateTime.now());

        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Expense not found for this user"));

        expenseRepository.delete(expense);
    }
}
