package com.travelbuddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelbuddy.dto.CommentRequestDto;
import com.travelbuddy.dto.CommentResponseDto;
import com.travelbuddy.service.CommentService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@Import(JwtService.class)  // ← Подключаем JwtService
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @Test
    @WithMockUser  // ← ИМИТИРУЕМ АВТОРИЗОВАННОГО ПОЛЬЗОВАТЕЛЯ!
    void addComment_ShouldReturnOk() throws Exception {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setMessage("Отличная поездка!");

        CommentResponseDto response = new CommentResponseDto();
        response.setId(1L);
        response.setMessage("Отличная поездка!");

        when(commentService.addComment(any(Long.class), any(String.class), any(CommentRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/trips/1/comments")
                        .principal(() -> "user@buddy.by")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
