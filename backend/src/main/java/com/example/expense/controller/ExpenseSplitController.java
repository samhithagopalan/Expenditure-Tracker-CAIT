package com.example.expense.controller;

import com.example.expense.dto.ExpenseSplitDTO;
import com.example.expense.model.ExpenseSplit;
import com.example.expense.service.ExpenseSplitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/expense-splits")
@CrossOrigin(origins = "*")
public class ExpenseSplitController {
    @Autowired
    private ExpenseSplitService expenseSplitService;

    @PostMapping
    public ResponseEntity<ExpenseSplitDTO> createExpenseSplit(@RequestBody ExpenseSplitDTO expenseSplitDTO) {
        try {
            ExpenseSplit split = expenseSplitService.createExpenseSplit(expenseSplitDTO.getExpenseId(),
                    expenseSplitDTO.getUserId(), expenseSplitDTO.getSplitAmount());
            ExpenseSplitDTO response = convertToDTO(split);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseSplitDTO> getExpenseSplitById(@PathVariable Long id) {
        Optional<ExpenseSplit> split = expenseSplitService.getExpenseSplitById(id);
        return split.map(s -> ResponseEntity.ok(convertToDTO(s)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/expense/{expenseId}")
    public ResponseEntity<List<ExpenseSplitDTO>> getSplitsByExpense(@PathVariable Long expenseId) {
        try {
            List<ExpenseSplit> splits = expenseSplitService.getSplitsByExpense(expenseId);
            List<ExpenseSplitDTO> splitDTOs = splits.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(splitDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseSplitDTO>> getSplitsByUser(@PathVariable Long userId) {
        try {
            List<ExpenseSplit> splits = expenseSplitService.getSplitsByUser(userId);
            List<ExpenseSplitDTO> splitDTOs = splits.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(splitDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<BigDecimal> getTotalSplitAmountsByUser(@PathVariable Long userId) {
        try {
            BigDecimal total = expenseSplitService.getTotalSplitAmountsByUser(userId);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/expense/{expenseId}/validate")
    public ResponseEntity<BigDecimal> validateSplitTotal(@PathVariable Long expenseId) {
        try {
            BigDecimal total = expenseSplitService.validateSplitTotal(expenseId);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpenseSplit(@PathVariable Long id) {
        expenseSplitService.deleteExpenseSplit(id);
        return ResponseEntity.noContent().build();
    }

    private ExpenseSplitDTO convertToDTO(ExpenseSplit split) {
        return ExpenseSplitDTO.builder()
                .id(split.getId())
                .expenseId(split.getExpense().getId())
                .userId(split.getUser().getId())
                .splitAmount(split.getSplitAmount())
                .build();
    }
}
