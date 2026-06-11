package com.example.expense.service;

import com.example.expense.model.Expense;
import com.example.expense.model.ExpenseSplit;
import com.example.expense.model.User;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.ExpenseSplitRepository;
import com.example.expense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseSplitService {
    @Autowired
    private ExpenseSplitRepository expenseSplitRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public ExpenseSplit createExpenseSplit(Long expenseId, Long userId, BigDecimal splitAmount) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (splitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Split amount must be greater than 0");
        }

        if (splitAmount.compareTo(expense.getAmount()) > 0) {
            throw new IllegalArgumentException("Split amount cannot exceed expense amount");
        }

        ExpenseSplit split = ExpenseSplit.builder()
                .expense(expense)
                .user(user)
                .splitAmount(splitAmount)
                .createdAt(LocalDateTime.now())
                .build();

        return expenseSplitRepository.save(split);
    }

    public Optional<ExpenseSplit> getExpenseSplitById(Long id) {
        return expenseSplitRepository.findById(id);
    }

    public List<ExpenseSplit> getSplitsByExpense(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        return expenseSplitRepository.findByExpense(expense);
    }

    public List<ExpenseSplit> getSplitsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return expenseSplitRepository.findByUser(user);
    }

    public BigDecimal getTotalSplitAmountsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BigDecimal total = expenseSplitRepository.sumSplitAmountsByUser(user);
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal validateSplitTotal(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        
        List<ExpenseSplit> splits = expenseSplitRepository.findByExpense(expense);
        BigDecimal totalSplit = splits.stream()
                .map(ExpenseSplit::getSplitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalSplit.compareTo(expense.getAmount()) > 0) {
            throw new IllegalArgumentException("Total split amount exceeds expense amount");
        }
        return totalSplit;
    }

    public void deleteExpenseSplit(Long id) {
        expenseSplitRepository.deleteById(id);
    }
}
