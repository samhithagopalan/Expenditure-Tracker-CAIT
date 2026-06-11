package com.example.expense.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplitDTO {
    private Long id;
    private Long expenseId;
    private Long userId;
    private BigDecimal splitAmount;
}
