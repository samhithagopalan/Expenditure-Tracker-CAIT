package com.example.expense.controller;

import com.example.expense.dto.ExpenseDTO;
import com.example.expense.model.Expense;
import com.example.expense.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(@RequestBody ExpenseDTO expenseDTO) {
        try {
            Expense expense = expenseService.createExpense(expenseDTO.getUserId(), expenseDTO.getCategoryId(),
                    expenseDTO.getDescription(), expenseDTO.getAmount(), expenseDTO.getExpenseDate());
            ExpenseDTO response = convertToDTO(expense);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long id) {
        Optional<Expense> expense = expenseService.getExpenseById(id);
        return expense.map(e -> ResponseEntity.ok(convertToDTO(e)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByUser(@PathVariable Long userId) {
        try {
            List<Expense> expenses = expenseService.getExpensesByUser(userId);
            List<ExpenseDTO> expenseDTOs = expenses.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(expenseDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<ExpenseDTO>> getExpensesByDateRange(
            @PathVariable Long userId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        try {
            List<Expense> expenses = expenseService.getExpensesByUserAndDateRange(userId, startDate, endDate);
            List<ExpenseDTO> expenseDTOs = expenses.stream().map(this::convertToDTO).collect(Collectors.toList());
            return ResponseEntity.ok(expenseDTOs);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user/{userId}/total-approved")
    public ResponseEntity<BigDecimal> getTotalApprovedExpenses(@PathVariable Long userId) {
        try {
            BigDecimal total = expenseService.getTotalApprovedExpensesByUser(userId);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO) {
        try {
            Expense expense = expenseService.updateExpense(id, expenseDTO.getUserId(), expenseDTO.getDescription(),
                    expenseDTO.getAmount(), expenseDTO.getStatus());
            ExpenseDTO response = convertToDTO(expense);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, @RequestParam Long userId) {
        try {
            expenseService.deleteExpense(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{id}/upload-receipt")
    public ResponseEntity<ExpenseDTO> uploadReceipt(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {

            String uploadDir = "uploads/receipts/";

            Files.createDirectories(
                    Paths.get(uploadDir));

            String fileName =
                    System.currentTimeMillis()
                            + "_"
                            + file.getOriginalFilename();

            Path filePath =
                    Paths.get(uploadDir, fileName);

            Files.write(
                    filePath,
                    file.getBytes());

            Expense expense =
                    expenseService.updateReceiptFile(
                            id,
                            fileName);

            return ResponseEntity.ok(
                    convertToDTO(expense));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(null);
        }
    }

    @DeleteMapping("/{id}/receipt")
        public ResponseEntity<ExpenseDTO>
        deleteReceipt(
                @PathVariable Long id) {

            Expense expense =
                    expenseService.deleteReceiptFile(id);

            return ResponseEntity.ok(
                    convertToDTO(expense));
        }
    @GetMapping("/export/{userId}")
        public ResponseEntity<byte[]>
        exportExpenses(
                @PathVariable Long userId)
                throws Exception {

            byte[] excelFile =
                    expenseService.exportExpensesToExcel(
                            userId);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=expenses.xlsx"
                    )
                    .contentType(
                            MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);
        }
    private ExpenseDTO convertToDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .receiptFile(expense.getReceiptFile())
                .userId(expense.getUser().getId())
                .categoryId(expense.getCategory().getId())
                .status(expense.getStatus().toString())
                .expenseDate(expense.getExpenseDate())
                .createdAt(expense.getCreatedAt())
                .updatedAt(expense.getUpdatedAt())
                .build();
    }
}
