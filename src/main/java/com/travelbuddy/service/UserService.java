package com.travelbuddy.service;


import com.travelbuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.travelbuddy.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.travelbuddy.entity.User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден с email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                AuthorityUtils.createAuthorityList(user.getRole())
        );

    }

    public User registerNewUser(User user) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("Данные пользователя не могут быть пустыми");
        }
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
        if (userExists) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        user.setRole("ROLE_USER");
       return userRepository.save(user);

    }
    public com.travelbuddy.repository.UserRepository getUserRepository() {
        return this.userRepository;
    }

}
