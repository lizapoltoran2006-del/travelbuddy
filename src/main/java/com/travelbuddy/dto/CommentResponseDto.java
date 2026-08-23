package com.travelbuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String message;
    private LocalDateTime createdAt;
    private String authorEmail;
    private String authorFullName;
}
