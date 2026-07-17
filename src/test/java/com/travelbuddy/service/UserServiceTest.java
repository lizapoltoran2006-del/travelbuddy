package com.travelbuddy.service;

import com.travelbuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void registerNewUser_Success() {
        // Given (Дано): Создаем тестового пользователя
        com.travelbuddy.entity.User user = new com.travelbuddy.entity.User();
        user.setEmail("test@buddy.by");
        user.setPassword("rawPassword");

        // Настраиваем поведение заглушек (Mocks)
        org.mockito.Mockito.when(userRepository.findByEmail("test@buddy.by"))
                .thenReturn(java.util.Optional.empty()); // База говорит: email свободен
        org.mockito.Mockito.when(passwordEncoder.encode("rawPassword"))
                .thenReturn("encodedPassword"); // Шифратор возвращает хэш
        org.mockito.Mockito.when(userRepository.save(user))
                .thenReturn(user); // Репозиторий успешно сохраняет

        // When (Действие): Вызываем метод сервиса
        com.travelbuddy.entity.User savedUser = userService.registerNewUser(user);

        // Then (Проверка): Убеждаемся, что логика отработала верно
        org.junit.jupiter.api.Assertions.assertNotNull(savedUser);
        org.junit.jupiter.api.Assertions.assertEquals("ROLE_USER", savedUser.getRole());
        org.junit.jupiter.api.Assertions.assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    public void registerNewUser_ThrowsException_WhenUserExists() {
        // Given (Дано): Пользователь с таким email уже зарегистрирован
        com.travelbuddy.entity.User user = new com.travelbuddy.entity.User();
        user.setEmail("existing@buddy.by");

        org.mockito.Mockito.when(userRepository.findByEmail("existing@buddy.by"))
                .thenReturn(java.util.Optional.of(new com.travelbuddy.entity.User())); // База нашла дубликат

        // When & Then (Действие и проверка): Ожидаем выброс ошибки RuntimeException
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            userService.registerNewUser(user);
        });
    }

    @Test
    public void loadUserByUsername_Success() {
        // Given
        com.travelbuddy.entity.User user = new com.travelbuddy.entity.User();
        user.setEmail("auth@buddy.by");
        user.setPassword("securePass");
        user.setRole("ROLE_USER");

        org.mockito.Mockito.when(userRepository.findByEmail("auth@buddy.by"))
                .thenReturn(java.util.Optional.of(user));

        // When
        org.springframework.security.core.userdetails.UserDetails userDetails =
                userService.loadUserByUsername("auth@buddy.by");

        // Then
        org.junit.jupiter.api.Assertions.assertNotNull(userDetails);
        org.junit.jupiter.api.Assertions.assertEquals("auth@buddy.by", userDetails.getUsername());
    }



}

