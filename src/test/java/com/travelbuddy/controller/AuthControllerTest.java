package com.travelbuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbuddy.dto.AuthRequestDto;
import com.travelbuddy.dto.AuthResponseDto;
import com.travelbuddy.dto.RegisterRequestDto;
import com.travelbuddy.dto.UserResponseDto;
import com.travelbuddy.service.AuthService;
import com.travelbuddy.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(JwtService.class)  // ← Подключаем JwtService
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @WithMockUser  // ← ИМИТИРУЕМ АВТОРИЗОВАННОГО ПОЛЬЗОВАТЕЛЯ!
    void register_ShouldReturnOk() throws Exception {
        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setEmail("test@buddy.by");
        dto.setPassword("password");
        dto.setFullName("Test User");

        UserResponseDto response = new UserResponseDto();
        response.setId(1L);
        response.setEmail("test@buddy.by");
        response.setFullName("Test User");

        when(authService.registerUser(any(RegisterRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@buddy.by"));
    }

    @Test
    @WithMockUser
    void login_ShouldReturnOk() throws Exception {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setEmail("test@buddy.by");
        dto.setPassword("password");

        when(authService.authenticate(any(AuthRequestDto.class)))
                .thenReturn(new AuthResponseDto("jwt-token"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    @WithMockUser
    void login_ShouldReturnBadRequest_WhenInvalid() throws Exception {
        AuthRequestDto dto = new AuthRequestDto();
        dto.setEmail("test@buddy.by");
        dto.setPassword("wrong");

        when(authService.authenticate(any(AuthRequestDto.class)))
                .thenThrow(new RuntimeException("Неверный email или пароль"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
