package com.example.expense.service;

import com.example.expense.model.*;
import com.example.expense.repository.ExpenseRepository;
import com.example.expense.repository.ExpenseSplitRepository;
import com.example.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExpenseSplitServiceTest {
    @Mock
    private ExpenseSplitRepository expenseSplitRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseSplitService expenseSplitService;

    private User user1;
    private User user2;
    private Expense expense;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = User.builder().id(1L).email("user1@example.com").name("User 1").build();
        user2 = User.builder().id(2L).email("user2@example.com").name("User 2").build();
        Category category = Category.builder().id(1L).name("Food").user(user1).build();
        expense = Expense.builder().id(1L).user(user1).category(category).description("Lunch").amount(BigDecimal.valueOf(100)).build();
    }

    @Test
    public void testCreateExpenseSplitSuccess() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(expenseSplitRepository.save(any(ExpenseSplit.class))).thenAnswer(invocation -> {
            ExpenseSplit split = invocation.getArgument(0);
            split.setId(1L);
            return split;
        });

        ExpenseSplit result = expenseSplitService.createExpenseSplit(1L, 2L, BigDecimal.valueOf(50));

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(50), result.getSplitAmount());
        verify(expenseSplitRepository, times(1)).save(any(ExpenseSplit.class));
    }

    @Test
    public void testCreateExpenseSplitInvalidAmount() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        assertThrows(IllegalArgumentException.class, () -> {
            expenseSplitService.createExpenseSplit(1L, 2L, BigDecimal.ZERO);
        });
    }

    @Test
    public void testCreateExpenseSplitExceedsExpenseAmount() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        assertThrows(IllegalArgumentException.class, () -> {
            expenseSplitService.createExpenseSplit(1L, 2L, BigDecimal.valueOf(150));
        });
    }

    @Test
    public void testGetExpenseSplitByIdSuccess() {
        ExpenseSplit split = ExpenseSplit.builder().id(1L).expense(expense).user(user2).splitAmount(BigDecimal.valueOf(50)).build();
        when(expenseSplitRepository.findById(1L)).thenReturn(Optional.of(split));

        Optional<ExpenseSplit> result = expenseSplitService.getExpenseSplitById(1L);

        assertTrue(result.isPresent());
        assertEquals(BigDecimal.valueOf(50), result.get().getSplitAmount());
    }

    @Test
    public void testGetSplitsByExpenseSuccess() {
        ExpenseSplit split1 = ExpenseSplit.builder().id(1L).expense(expense).user(user2).splitAmount(BigDecimal.valueOf(50)).build();
        ExpenseSplit split2 = ExpenseSplit.builder().id(2L).expense(expense).user(user1).splitAmount(BigDecimal.valueOf(50)).build();
        List<ExpenseSplit> splits = new ArrayList<>();
        splits.add(split1);
        splits.add(split2);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseSplitRepository.findByExpense(expense)).thenReturn(splits);

        List<ExpenseSplit> result = expenseSplitService.getSplitsByExpense(1L);

        assertEquals(2, result.size());
    }

    @Test
    public void testValidateSplitTotalSuccess() {
        ExpenseSplit split1 = ExpenseSplit.builder().id(1L).expense(expense).user(user2).splitAmount(BigDecimal.valueOf(50)).build();
        ExpenseSplit split2 = ExpenseSplit.builder().id(2L).expense(expense).user(user1).splitAmount(BigDecimal.valueOf(50)).build();
        List<ExpenseSplit> splits = new ArrayList<>();
        splits.add(split1);
        splits.add(split2);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseSplitRepository.findByExpense(expense)).thenReturn(splits);

        BigDecimal result = expenseSplitService.validateSplitTotal(1L);

        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    public void testValidateSplitTotalExceedsExpense() {
        ExpenseSplit split1 = ExpenseSplit.builder().id(1L).expense(expense).user(user2).splitAmount(BigDecimal.valueOf(75)).build();
        ExpenseSplit split2 = ExpenseSplit.builder().id(2L).expense(expense).user(user1).splitAmount(BigDecimal.valueOf(50)).build();
        List<ExpenseSplit> splits = new ArrayList<>();
        splits.add(split1);
        splits.add(split2);

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));
        when(expenseSplitRepository.findByExpense(expense)).thenReturn(splits);

        assertThrows(IllegalArgumentException.class, () -> {
            expenseSplitService.validateSplitTotal(1L);
        });
    }

    @Test
    public void testGetTotalSplitAmountsByUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(expenseSplitRepository.sumSplitAmountsByUser(user1)).thenReturn(BigDecimal.valueOf(200));

        BigDecimal result = expenseSplitService.getTotalSplitAmountsByUser(1L);

        assertEquals(BigDecimal.valueOf(200), result);
    }

    @Test
    public void testDeleteExpenseSplitSuccess() {
        expenseSplitService.deleteExpenseSplit(1L);
        verify(expenseSplitRepository, times(1)).deleteById(1L);
    }
}
