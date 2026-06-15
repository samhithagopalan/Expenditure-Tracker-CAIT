package com.example.expense.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileSummaryDTO {

    private Long id;
    private String name;
    private String email;
    private String profilePicture;
    private Long totalCategories;
    private Long totalBudgets;

    private BigDecimal totalExpenses;

    private LocalDateTime memberSince;
}