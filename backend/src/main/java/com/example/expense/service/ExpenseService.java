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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;

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
    public Expense updateReceiptFile(Long expenseId,
                                        String receiptFile) {

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found"));

        expense.setReceiptFile(receiptFile);
        expense.setUpdatedAt(LocalDateTime.now());

        return expenseRepository.save(expense);
        }
    public Expense deleteReceiptFile(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found"));

        expense.setReceiptFile(null);
        expense.setUpdatedAt(LocalDateTime.now());

        return expenseRepository.save(expense);
        }
    public byte[] exportExpensesToExcel(Long userId)
                throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<Expense> expenses =
                expenseRepository.findByUser(user);

        Workbook workbook =
                new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet("Expenses");

        Row header =
                sheet.createRow(0);

        header.createCell(0)
                .setCellValue("Description");

        header.createCell(1)
                .setCellValue("Amount");

        header.createCell(2)
                .setCellValue("Category");

        header.createCell(3)
                .setCellValue("Status");

        header.createCell(4)
                .setCellValue("Date");

        header.createCell(5)
                .setCellValue("Receipt");

        int rowNum = 1;

        for (Expense expense : expenses) {

                Row row =
                        sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(expense.getDescription());

                row.createCell(1)
                        .setCellValue(
                                expense.getAmount().doubleValue());

                row.createCell(2)
                        .setCellValue(
                                expense.getCategory().getName());

                row.createCell(3)
                        .setCellValue(
                                expense.getStatus().toString());

                row.createCell(4)
                        .setCellValue(
                                expense.getExpenseDate().toString());

                row.createCell(5)
                        .setCellValue(
                                expense.getReceiptFile() != null
                                        ? "Yes"
                                        : "No");
        }

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        workbook.write(out);

        workbook.close();

        return out.toByteArray();
        }
}
