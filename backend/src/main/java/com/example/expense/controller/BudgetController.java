package com.example.expense.controller;

import com.example.expense.dto.BudgetDTO;
import com.example.expense.model.Budget;
import com.example.expense.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping
    public ResponseEntity<BudgetDTO> createBudget(
            @RequestBody BudgetDTO dto) {

        Budget budget = budgetService.createBudget(
                dto.getUserId(),
                dto.getCategoryId(),
                dto.getBudgetAmount()
        );

        return ResponseEntity.ok(convertToDTO(budget));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetDTO>> getBudgets(
            @PathVariable Long userId) {

        List<BudgetDTO> budgets =
                budgetService.getBudgetsByUser(userId)
                        .stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(budgets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetDTO> updateBudget(
            @PathVariable Long id,
            @RequestBody BudgetDTO dto) {

        Budget budget =
                budgetService.updateBudget(
                        id,
                        dto.getBudgetAmount());

        return ResponseEntity.ok(convertToDTO(budget));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            @PathVariable Long id) {

        budgetService.deleteBudget(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/actual-spent")
    public ResponseEntity<BigDecimal> getActualSpent(
            @RequestParam Long userId,
            @RequestParam Long categoryId) {

        return ResponseEntity.ok(
                budgetService.getActualSpent(
                        userId,
                        categoryId
                )
        );
    }

    private BudgetDTO convertToDTO(Budget budget) {

        return BudgetDTO.builder()
                .id(budget.getId())
                .userId(budget.getUser().getId())
                .categoryId(budget.getCategory().getId())
                .budgetAmount(budget.getBudgetAmount())
                .build();
    }

    @GetMapping("/summary/{userId}")
    public ResponseEntity<List<com.example.expense.dto.BudgetSummaryDTO>>
    getBudgetSummary(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                budgetService.getBudgetSummary(userId)
        );
    }
}