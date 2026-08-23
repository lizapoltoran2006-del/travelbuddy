package com.travelbuddy.controller;

import com.travelbuddy.dto.ReviewRequestDto;
import com.travelbuddy.dto.ReviewResponseDto;
import com.travelbuddy.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponseDto addReview(@RequestBody ReviewRequestDto requestDto, Principal principal) {
        String userEmail = principal.getName();
        return reviewService.addReview(userEmail, requestDto);
    }

    @GetMapping("/user/{userId}")
    public List<ReviewResponseDto> getReviewsByUser(@PathVariable Long userId) {
        return reviewService.getReviewsByUser(userId);
    }

    @GetMapping("/user/{userId}/average")
    public Double getAverageRating(@PathVariable Long userId) {
        return reviewService.getAverageRating(userId);
    }

    @DeleteMapping("/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId, Principal principal) {
        String userEmail = principal.getName();
        reviewService.deleteReview(reviewId, userEmail);
        return "Отзыв удален";
    }
}
