package com.travelbuddy.service;

import com.travelbuddy.dto.AuthRequestDto;
import com.travelbuddy.dto.AuthResponseDto;
import com.travelbuddy.dto.RegisterRequestDto;
import com.travelbuddy.dto.UserResponseDto;
import com.travelbuddy.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDto authenticate(AuthRequestDto requestDto) {

        User user = userService.findUserByEmail(requestDto.getEmail());


        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Неверный email или пароль");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        return new AuthResponseDto(token);
    }

    public UserResponseDto registerUser(RegisterRequestDto requestDto) {

        return userService.registerNewUser(requestDto);
    }
}
