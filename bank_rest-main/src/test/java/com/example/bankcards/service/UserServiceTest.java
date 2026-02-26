package com.example.bankcards.service;

import com.example.bankcards.dto.user.CreateUserRequest;
import com.example.bankcards.dto.user.UpdateUserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("create should save new user when username is unique")
    void create_success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("user");
        request.setPassword("pass");
        request.setRole("ADMIN");

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        User saved = new User();
        saved.setId(1L);
        saved.setUsername("user");
        saved.setPassword("encoded");
        saved.setRole(Role.ADMIN);

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.create(request);

        assertEquals(1L, result.getId());
        assertEquals("user", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    @DisplayName("create should throw when username already exists")
    void create_usernameExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("user");
        request.setPassword("pass");
        request.setRole("USER");

        when(userRepository.existsByUsername("user")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.create(request));
    }

    @Test
    @DisplayName("getById should return user when found")
    void getById_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("user", result.getUsername());
    }

    @Test
    @DisplayName("getById should throw when user not found")
    void getById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.getById(1L));
    }

    @Test
    @DisplayName("update should change password and role when provided")
    void update_success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("old");
        user.setRole(Role.USER);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setPassword("new");
        request.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new")).thenReturn("encoded");

        User result = userService.update(1L, request);

        assertEquals("encoded", result.getPassword());
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    @DisplayName("delete should remove user when exists")
    void delete_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete should throw when user not found")
    void delete_notFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> userService.delete(1L));
    }
}

