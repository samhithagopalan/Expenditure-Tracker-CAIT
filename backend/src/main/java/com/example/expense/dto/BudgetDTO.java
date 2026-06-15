package com.example.expense.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BudgetDTO {

    private Long id;
    private Long userId;
    private Long categoryId;
    private BigDecimal budgetAmount;
}