package com.travelbuddy.service;

import com.travelbuddy.dto.RegisterRequestDto;
import com.travelbuddy.dto.UserResponseDto;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RegisterRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new RegisterRequestDto();
        requestDto.setEmail("new@buddy.by");
        requestDto.setPassword("rawPass");
        requestDto.setFullName("New User");
        requestDto.setAge(25);
        requestDto.setContactInfo("@telegram");
    }

    @Test
    void registerNewUser_ShouldSave_WhenValid() {
        when(userRepository.findByEmail("new@buddy.by")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPass")).thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("new@buddy.by");
        savedUser.setFullName("New User");
        savedUser.setRole("ROLE_USER");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto response = userService.registerNewUser(requestDto);

        assertNotNull(response);
        assertEquals("new@buddy.by", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerNewUser_ShouldThrow_WhenEmailExists() {
        when(userRepository.findByEmail("new@buddy.by")).thenReturn(Optional.of(new User()));
        assertThrows(RuntimeException.class, () -> userService.registerNewUser(requestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenExists() {
        User user = new User();
        user.setEmail("test@buddy.by");
        user.setPassword("pass");
        user.setRole("ROLE_USER");

        when(userRepository.findByEmail("test@buddy.by")).thenReturn(Optional.of(user));

        var details = userService.loadUserByUsername("test@buddy.by");
        assertNotNull(details);
        assertEquals("test@buddy.by", details.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenNotFound() {
        when(userRepository.findByEmail("unknown@buddy.by")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.loadUserByUsername("unknown@buddy.by"));
    }
}

