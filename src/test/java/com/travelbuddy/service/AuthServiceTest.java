package com.travelbuddy.service;

import com.travelbuddy.dto.AuthRequestDto;
import com.travelbuddy.dto.AuthResponseDto;
import com.travelbuddy.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private AuthRequestDto authRequestDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@buddy.by");
        testUser.setPassword("encodedPass");
        testUser.setRole("ROLE_USER");

        authRequestDto = new AuthRequestDto();
        authRequestDto.setEmail("test@buddy.by");
        authRequestDto.setPassword("rawPass");
    }

    @Test
    void authenticate_ShouldReturnToken_WhenValid() {
        when(userService.findUserByEmail("test@buddy.by")).thenReturn(testUser);
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(true);
        when(jwtService.generateToken("test@buddy.by", "ROLE_USER")).thenReturn("jwt-token");

        AuthResponseDto response = authService.authenticate(authRequestDto);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void authenticate_ShouldThrow_WhenPasswordInvalid() {
        when(userService.findUserByEmail("test@buddy.by")).thenReturn(testUser);
        when(passwordEncoder.matches("rawPass", "encodedPass")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.authenticate(authRequestDto));
    }

    @Test
    void authenticate_ShouldThrow_WhenUserNotFound() {
        when(userService.findUserByEmail("test@buddy.by"))
                .thenThrow(new RuntimeException("Пользователь не найден"));

        assertThrows(RuntimeException.class, () -> authService.authenticate(authRequestDto));
    }
}
