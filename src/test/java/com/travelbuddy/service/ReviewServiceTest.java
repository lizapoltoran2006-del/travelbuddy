package com.travelbuddy.service;

import com.travelbuddy.dto.ReviewRequestDto;
import com.travelbuddy.dto.ReviewResponseDto;
import com.travelbuddy.entity.Review;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.ReviewRepository;
import com.travelbuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ReviewService reviewService;

    private User author, target;
    private Review review;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        author.setEmail("a@buddy.by");
        author.setFullName("Author");

        target = new User();
        target.setId(2L);
        target.setEmail("t@buddy.by");
        target.setFullName("Target");

        review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setText("Good");
        review.setAuthor(author);
        review.setTarget(target);
    }

    @Test
    void addReview_ShouldSave() {
        when(userRepository.findByEmail("a@buddy.by")).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(applicationService.hasCompletedTripWithDriver(1L, 2L)).thenReturn(true);
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewRequestDto dto = new ReviewRequestDto(5, "Good", 2L);
        ReviewResponseDto response = reviewService.addReview("a@buddy.by", dto);

        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Good", response.getText());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void addReview_ShouldThrow_WhenSelfReview() {
        when(userRepository.findByEmail("a@buddy.by")).thenReturn(Optional.of(author));
        when(userRepository.findById(1L)).thenReturn(Optional.of(author));

        ReviewRequestDto dto = new ReviewRequestDto(5, "Good", 1L);
        assertThrows(RuntimeException.class, () -> reviewService.addReview("a@buddy.by", dto));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void addReview_ShouldThrow_WhenRatingInvalid() {
        when(userRepository.findByEmail("a@buddy.by")).thenReturn(Optional.of(author));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));

        ReviewRequestDto dto = new ReviewRequestDto(10, "Good", 2L);
        assertThrows(RuntimeException.class, () -> reviewService.addReview("a@buddy.by", dto));
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getAverageRating_ShouldCalculate() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(reviewRepository.findByTargetId(2L)).thenReturn(List.of(review));

        Double average = reviewService.getAverageRating(2L);
        assertEquals(5.0, average);
    }

    @Test
    void getAverageRating_ShouldReturnZero_WhenNoReviews() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(reviewRepository.findByTargetId(2L)).thenReturn(List.of());

        Double average = reviewService.getAverageRating(2L);
        assertEquals(0.0, average);
    }
}
