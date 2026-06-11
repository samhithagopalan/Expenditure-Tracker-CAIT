package com.example.expense.service;

import com.example.expense.model.Category;
import com.example.expense.model.User;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).email("test@example.com").name("Test").build();
    }

    @Test
    public void testCreateCategorySuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category category = invocation.getArgument(0);
            category.setId(1L);
            return category;
        });

        Category result = categoryService.createCategory(1L, "Food", "Food expenses");

        assertNotNull(result);
        assertEquals("Food", result.getName());
        assertEquals("Food expenses", result.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void testCreateCategoryUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            categoryService.createCategory(999L, "Food", "Food expenses");
        });
    }

    @Test
    public void testGetCategoryByIdSuccess() {
        Category category = Category.builder().id(1L).name("Food").user(user).build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Optional<Category> result = categoryService.getCategoryById(1L);

        assertTrue(result.isPresent());
        assertEquals("Food", result.get().getName());
    }

    @Test
    public void testUpdateCategorySuccess() {
        Category category = Category.builder().id(1L).name("Old Food").description("Old description").user(user).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.updateCategory(1L, 1L, "New Food", "New description");

        assertEquals("New Food", result.getName());
        assertEquals("New description", result.getDescription());
    }

    @Test
    public void testDeleteCategorySuccess() {
        Category category = Category.builder().id(1L).name("Food").user(user).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(category));

        assertDoesNotThrow(() -> categoryService.deleteCategory(1L, 1L));
        verify(categoryRepository, times(1)).delete(category);
    }
}
