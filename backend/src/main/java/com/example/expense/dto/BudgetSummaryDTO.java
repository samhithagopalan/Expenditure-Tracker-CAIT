package com.example.expense.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetSummaryDTO {

    private Long budgetId;

    private Long categoryId;

    private String categoryName;

    private BigDecimal budgetAmount;

    private BigDecimal spentAmount;

    private BigDecimal difference;

    private boolean overBudget;
}