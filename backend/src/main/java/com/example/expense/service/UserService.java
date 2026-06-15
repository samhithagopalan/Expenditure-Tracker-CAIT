package com.example.expense.service;

import com.example.expense.dto.ProfileSummaryDTO;
import com.example.expense.model.User;
import com.example.expense.repository.UserRepository;
import com.example.expense.repository.CategoryRepository;
import com.example.expense.repository.BudgetRepository;
import com.example.expense.repository.ExpenseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    public User createUser(String email, String name, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(email)
                .name(name)
                .password(password)
                .profilePicture(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id,
                           String name,
                           String password) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (name != null) {
            user.setName(name);
        }

        if (password != null) {
            user.setPassword(password);
        }

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public ProfileSummaryDTO getProfileSummary(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Long totalCategories =
                categoryRepository.countByUser(user);

        Long totalBudgets =
                budgetRepository.countByUser(user);

        BigDecimal totalExpenses =
                expenseRepository.sumAllExpensesByUser(user);

        return ProfileSummaryDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .memberSince(user.getCreatedAt())
                .totalCategories(totalCategories)
                .totalBudgets(totalBudgets)
                .totalExpenses(totalExpenses)
                .build();
    }
    public User updateProfilePicture(Long id,
                                    String profilePicture) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setProfilePicture(profilePicture);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    public User deleteProfilePicture(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        user.setProfilePicture(null);

        return userRepository.save(user);
    }
}