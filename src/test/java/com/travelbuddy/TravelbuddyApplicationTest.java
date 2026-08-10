package com.travelbuddy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TravelbuddyApplicationTest {

    @Test
    void contextLoads() {
        // Просто проверяем, что контекст загружается
        assertTrue(true);
    }

    @Test
    void main_ShouldStartApplication() {
        // Проверяем, что метод main не падает
        assertDoesNotThrow(() -> {
            TravelbuddyApplication.main(new String[]{});
        });
    }
}
