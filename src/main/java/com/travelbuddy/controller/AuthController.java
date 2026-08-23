package com.travelbuddy.controller;

import com.travelbuddy.dto.AuthRequestDto;
import com.travelbuddy.dto.AuthResponseDto;
import com.travelbuddy.dto.RegisterRequestDto;
import com.travelbuddy.dto.UserResponseDto;
import com.travelbuddy.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public UserResponseDto register(@RequestBody RegisterRequestDto requestDto) {
        return authService.registerUser(requestDto);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto requestDto) {
        return authService.authenticate(requestDto);
    }
}
