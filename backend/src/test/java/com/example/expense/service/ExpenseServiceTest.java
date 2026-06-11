package com.example.expense.service;

import com.example.expense.model.*;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {
    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User user;
    private Category category;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).email("test@example.com").name("Test").build();
        category = Category.builder().id(1L).name("Food").user(user).build();
    }

    @Test
    public void testCreateExpenseSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> {
            Expense expense = invocation.getArgument(0);
            expense.setId(1L);
            return expense;
        });

        Expense result = expenseService.createExpense(1L, 1L, "Lunch", BigDecimal.valueOf(50), LocalDateTime.now());

        assertNotNull(result);
        assertEquals("Lunch", result.getDescription());
        assertEquals(BigDecimal.valueOf(50), result.getAmount());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    public void testCreateExpenseInvalidAmount() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(IllegalArgumentException.class, () -> {
            expenseService.createExpense(1L, 1L, "Lunch", BigDecimal.ZERO, LocalDateTime.now());
        });
    }

    @Test
    public void testCreateExpenseUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            expenseService.createExpense(999L, 1L, "Lunch", BigDecimal.valueOf(50), LocalDateTime.now());
        });
    }

    @Test
    public void testGetExpenseByIdSuccess() {
        Expense expense = Expense.builder().id(1L).user(user).category(category).description("Lunch").amount(BigDecimal.valueOf(50)).build();
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        Optional<Expense> result = expenseService.getExpenseById(1L);

        assertTrue(result.isPresent());
        assertEquals("Lunch", result.get().getDescription());
    }

    @Test
    public void testUpdateExpenseSuccess() {
        Expense expense = Expense.builder().id(1L).user(user).category(category).description("Lunch").amount(BigDecimal.valueOf(50)).status(ExpenseStatus.PENDING).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(expenseRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(expense));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        Expense result = expenseService.updateExpense(1L, 1L, "Updated", BigDecimal.valueOf(75), "APPROVED");

        assertEquals("Updated", result.getDescription());
        assertEquals(BigDecimal.valueOf(75), result.getAmount());
        assertEquals(ExpenseStatus.APPROVED, result.getStatus());
    }

    @Test
    public void testDeleteExpenseSuccess() {
        Expense expense = Expense.builder().id(1L).user(user).category(category).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(expenseRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(expense));

        assertDoesNotThrow(() -> expenseService.deleteExpense(1L, 1L));
        verify(expenseRepository, times(1)).delete(expense);
    }

    @Test
    public void testGetTotalApprovedExpensesByUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(expenseRepository.sumApprovedExpensesByUser(user)).thenReturn(BigDecimal.valueOf(150));

        BigDecimal result = expenseService.getTotalApprovedExpensesByUser(1L);

        assertEquals(BigDecimal.valueOf(150), result);
    }
}
