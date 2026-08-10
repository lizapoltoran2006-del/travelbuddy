package com.travelbuddy.service;

import com.travelbuddy.entity.User;
import com.travelbuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@buddy.by");
        testUser.setPassword("rawPassword");
        testUser.setFullName("Test User");
        testUser.setAge(25);
        testUser.setContactInfo("@telegram");
    }

    @Test
    void registerNewUser_ShouldSaveUser_WhenEmailIsFree() {
        when(userRepository.findByEmail("test@buddy.by")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        User savedUser = userService.registerNewUser(testUser);

        assertNotNull(savedUser);
        assertEquals(1L, savedUser.getId());
        assertEquals("test@buddy.by", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("ROLE_USER", savedUser.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerNewUser_ShouldThrowException_WhenUserExists() {
        when(userRepository.findByEmail("test@buddy.by")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.registerNewUser(testUser));
        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerNewUser_ShouldThrowException_WhenUserIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerNewUser(null));
        assertEquals("Данные пользователя не могут быть пустыми", exception.getMessage());
    }

    @Test
    void registerNewUser_ShouldThrowException_WhenEmailIsNull() {
        testUser.setEmail(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerNewUser(testUser));
        assertEquals("Данные пользователя не могут быть пустыми", exception.getMessage());
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        testUser.setPassword("encodedPassword");
        testUser.setRole("ROLE_USER");
        when(userRepository.findByEmail("test@buddy.by")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("test@buddy.by");

        assertNotNull(userDetails);
        assertEquals("test@buddy.by", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail("unknown@buddy.by")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("unknown@buddy.by"));
        assertEquals("Пользователь не найден с email: unknown@buddy.by", exception.getMessage());
    }

    @Test
    void getUserRepository_ShouldReturnRepository() {
        UserRepository repository = userService.getUserRepository();
        assertEquals(userRepository, repository);
    }
}

