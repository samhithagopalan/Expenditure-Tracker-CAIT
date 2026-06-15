package com.example.expense.controller;

import com.example.expense.dto.UserDTO;
import com.example.expense.model.User;
import com.example.expense.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.example.expense.dto.ProfileSummaryDTO;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        try {
            User user = userService.createUser(userDTO.getEmail(), userDTO.getName(), userDTO.getPassword());
            UserDTO response = convertToDTO(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(u -> ResponseEntity.ok(convertToDTO(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            User user = userService.updateUser(id, userDTO.getName(), userDTO.getPassword());
            UserDTO response = convertToDTO(user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload-picture")
    public ResponseEntity<UserDTO> uploadProfilePicture(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        try {

            String uploadDir = "uploads/profile-pictures/";

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

            User user =
                    userService.updateProfilePicture(
                            id,
                            fileName);

            return ResponseEntity.ok(
                    convertToDTO(user));

        } catch (Exception e) {

            return ResponseEntity.badRequest()
                    .body(null);
        }
    }
    @GetMapping("/{id}/profile")
    public ResponseEntity<ProfileSummaryDTO>
    getProfileSummary(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.getProfileSummary(id)
        );
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .password(user.getPassword())
            .profilePicture(user.getProfilePicture())
            .build();
    }
    @DeleteMapping("/{id}/profile-picture")
    public ResponseEntity<UserDTO>
    deleteProfilePicture(
            @PathVariable Long id) {

        User user =
                userService.deleteProfilePicture(id);

        return ResponseEntity.ok(
                convertToDTO(user));
    }
}
