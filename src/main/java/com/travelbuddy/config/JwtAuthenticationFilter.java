package com.travelbuddy.config;

import com.travelbuddy.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Достаем заголовок Authorization из запроса
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Если заголовка нет или он не начинается со слова Bearer, пропускаем запрос дальше
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Вырезаем сам токен из строки (убираем "Bearer ")
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractEmail(jwt);

        // 4. Если email успешно извлечен и пользователь еще не авторизован в текущем потоке
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Проверяем валидность токена
            if (jwtService.isTokenValid(jwt, userEmail)) {
                // Извлекаем роль (мы зашивали её в JwtService с ключом "role")
                // В реальном приложении здесь можно извлечь роль напрямую из токена
                // Для простоты выставим стандартную роль, так как у нас гибкие аккаунты
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail,
                        null,
                        AuthorityUtils.createAuthorityList("ROLE_USER")
                );

                // Сохраняем авторизацию в контекст Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Передаем управление следующему фильтру в цепочке
        filterChain.doFilter(request, response);
    }
}

