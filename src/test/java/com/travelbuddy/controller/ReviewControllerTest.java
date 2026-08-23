package com.travelbuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbuddy.dto.ReviewRequestDto;
import com.travelbuddy.dto.ReviewResponseDto;
import com.travelbuddy.service.JwtService;
import com.travelbuddy.service.ReviewService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import(JwtService.class)  // ← Подключаем JwtService
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    @WithMockUser  // ← ИМИТИРУЕМ АВТОРИЗОВАННОГО ПОЛЬЗОВАТЕЛЯ!
    void addReview_ShouldReturnOk() throws Exception {
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setRating(5);
        dto.setText("Отличный попутчик!");
        dto.setTargetUserId(2L);

        ReviewResponseDto response = new ReviewResponseDto();
        response.setId(1L);
        response.setRating(5);
        response.setText("Отличный попутчик!");

        when(reviewService.addReview(any(String.class), any(ReviewRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/reviews")
                        .principal(() -> "author@buddy.by")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}