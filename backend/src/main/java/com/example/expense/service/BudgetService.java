package com.example.expense.service;

import com.example.expense.model.Budget;
import com.example.expense.model.Category;
import com.example.expense.model.User;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public Budget createBudget(Long userId,
                               Long categoryId,
                               BigDecimal budgetAmount) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
                
        budgetRepository
                .findByUserAndCategory(user, category)
                .ifPresent(b -> {
                    throw new RuntimeException(
                            "Budget already exists for this category");
                });

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .budgetAmount(budgetAmount)
                .build();

        return budgetRepository.save(budget);
    }

    public List<Budget> getBudgetsByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return budgetRepository.findByUser(user);
    }

    public Budget updateBudget(Long id,
                               BigDecimal budgetAmount) {

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        budget.setBudgetAmount(budgetAmount);

        return budgetRepository.save(budget);
    }

    public void deleteBudget(Long id) {

        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        budgetRepository.delete(budget);
    }

    public BigDecimal getActualSpent(Long userId,
                                     Long categoryId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return expenseRepository
                .sumExpensesByUserAndCategory(user, category);
    }
    public List<com.example.expense.dto.BudgetSummaryDTO> getBudgetSummary(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Budget> budgets = budgetRepository.findByUser(user);

        return budgets.stream().map(budget -> {

            BigDecimal spent =
                    expenseRepository.sumExpensesByUserAndCategory(
                            user,
                            budget.getCategory()
                    );

            spent = spent == null ? BigDecimal.ZERO : spent;

            BigDecimal difference =
                    spent.subtract(budget.getBudgetAmount());

            return com.example.expense.dto.BudgetSummaryDTO.builder()
                    .budgetId(budget.getId())
                    .categoryId(budget.getCategory().getId())
                    .categoryName(budget.getCategory().getName())
                    .budgetAmount(budget.getBudgetAmount())
                    .spentAmount(spent)
                    .difference(difference.abs())
                    .overBudget(
                            spent.compareTo(budget.getBudgetAmount()) > 0
                    )
                    .build();
        }).toList();
    }
}