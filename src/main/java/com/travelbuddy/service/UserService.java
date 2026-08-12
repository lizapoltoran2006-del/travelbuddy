package com.travelbuddy.service;


import com.travelbuddy.dto.RegisterRequestDto;
import com.travelbuddy.dto.UserResponseDto;
import com.travelbuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.travelbuddy.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole())
        );

    }

    // работает с DTO, возвращает DTO
    public UserResponseDto registerNewUser(RegisterRequestDto requestDto) {
        // 1. Проверяем, что данные не пустые
        if (requestDto == null || requestDto.getEmail() == null) {
            throw new IllegalArgumentException("Данные пользователя не могут быть пустыми");
        }

        // 2. Проверяем, что email свободен
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        if (requestDto.getPassword() == null || requestDto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Пароль не может быть пустым");
        }

        // 3. Создаем Entity из DTO
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword())); // Хешируем пароль!
        user.setFullName(requestDto.getFullName());
        user.setAge(requestDto.getAge());
        user.setContactInfo(requestDto.getContactInfo());
        user.setRole("ROLE_USER"); //  роль устанавливаем МЫ, а не клиент!

        // 4. Сохраняем в БД
        User savedUser = userRepository.save(user);

        // 5. Преобразуем Entity → DTO (скрываем пароль)
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(savedUser.getId());
        responseDto.setEmail(savedUser.getEmail());
        responseDto.setFullName(savedUser.getFullName());
        responseDto.setAge(savedUser.getAge());
        responseDto.setContactInfo(savedUser.getContactInfo());
        responseDto.setRole(savedUser.getRole());

        return responseDto;
    }

    public User findUserByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с email: " + email));
    }

}
