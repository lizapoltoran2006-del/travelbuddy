package com.travelbuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private Integer rating;
    private String text;
    private String authorEmail;
    private String authorFullName;
    private String targetEmail;
    private String targetFullName;
}
