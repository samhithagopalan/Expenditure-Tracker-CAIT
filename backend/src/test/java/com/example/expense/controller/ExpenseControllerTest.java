package com.example.expense.controller;

import com.example.expense.dto.ExpenseDTO;
import com.example.expense.model.*;
import com.example.expense.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ExpenseControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private ExpenseController expenseController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
    }

    @Test
    public void testCreateExpenseSuccess() throws Exception {
        ExpenseDTO expenseDTO = ExpenseDTO.builder()
                .description("Lunch")
                .amount(BigDecimal.valueOf(50))
                .userId(1L)
                .categoryId(1L)
                .expenseDate(LocalDateTime.now())
                .build();

        User user = User.builder().id(1L).build();
        Category category = Category.builder().id(1L).user(user).build();
        Expense expense = Expense.builder().id(1L).user(user).category(category).description("Lunch").amount(BigDecimal.valueOf(50)).build();

        when(expenseService.createExpense(1L, 1L, "Lunch", BigDecimal.valueOf(50), expenseDTO.getExpenseDate())).thenReturn(expense);

        mockMvc.perform(post("/api/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Lunch"));
    }

    @Test
    public void testGetExpenseByIdSuccess() throws Exception {
        User user = User.builder().id(1L).build();
        Category category = Category.builder().id(1L).user(user).build();
        Expense expense = Expense.builder().id(1L).user(user).category(category).description("Lunch").amount(BigDecimal.valueOf(50)).build();

        when(expenseService.getExpenseById(1L)).thenReturn(Optional.of(expense));

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Lunch"));
    }

    @Test
    public void testGetExpenseByIdNotFound() throws Exception {
        when(expenseService.getExpenseById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/expenses/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateExpenseSuccess() throws Exception {
        ExpenseDTO expenseDTO = ExpenseDTO.builder()
                .description("Updated Lunch")
                .amount(BigDecimal.valueOf(75))
                .status("APPROVED")
                .build();

        User user = User.builder().id(1L).build();
        Category category = Category.builder().id(1L).user(user).build();
        Expense expense = Expense.builder().id(1L).user(user).category(category).description("Updated Lunch").amount(BigDecimal.valueOf(75)).status(ExpenseStatus.APPROVED).build();

        when(expenseService.updateExpense(1L, 1L, "Updated Lunch", BigDecimal.valueOf(75), "APPROVED")).thenReturn(expense);

        mockMvc.perform(put("/api/expenses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Lunch"));
    }

    @Test
    public void testDeleteExpenseSuccess() throws Exception {
        mockMvc.perform(delete("/api/expenses/1?userId=1"))
                .andExpect(status().isNoContent());

        verify(expenseService, times(1)).deleteExpense(1L, 1L);
    }
}
