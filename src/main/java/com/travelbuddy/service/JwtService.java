package com.travelbuddy.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Секретный ключ для подписи токенов
    private final String SECRET_STRING = "superSecureAndVeryLongSecretKeyForTravelBuddyApplication2026";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));

    // Время жизни токена
    private final long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 365;

    //  токен на основе Email пользователя и зашивает туда его Роль
    public String generateToken(String email, String role) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", role);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    // Извлекает Email  из токена
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // извлекает роль из токена
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Проверяет, валиден ли токен
    public boolean isTokenValid(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    // Проверяет, просрочен ли токен
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Вспомогательный метод для извлечения любых данных (Claims) из токена
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Парсит токен с использованием нашего секретного ключа
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

