package com.travelbuddy.service;

import com.travelbuddy.dto.ReviewRequestDto;
import com.travelbuddy.dto.ReviewResponseDto;
import com.travelbuddy.entity.Review;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.ReviewRepository;
import com.travelbuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;

    @Transactional
    public ReviewResponseDto addReview(String authorEmail, ReviewRequestDto requestDto) {
        // 1. Находим автора отзыва
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new RuntimeException("Автор не найден"));

        // 2. Находим того, кому пишут отзыв
        User target = userRepository.findById(requestDto.getTargetUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 3. Проверяем, что автор не пишет отзыв сам себе
        if (author.getId().equals(target.getId())) {
            throw new RuntimeException("Нельзя оставить отзыв самому себе");
        }

        if (!applicationService.hasCompletedTripWithDriver(author.getId(), target.getId())) {
            throw new RuntimeException("Вы можете оставить отзыв только после завершённой поездки с этим пользователем");
        }

        // 4. Проверяем рейтинг
        if (requestDto.getRating() == null || requestDto.getRating() < 1 || requestDto.getRating() > 5) {
            throw new RuntimeException("Рейтинг должен быть от 1 до 5");
        }

        // 5. Создаем отзыв
        Review review = new Review();
        review.setRating(requestDto.getRating());
        review.setText(requestDto.getText());
        review.setAuthor(author);
        review.setTarget(target);

        Review savedReview = reviewRepository.save(review);

        // 6. Преобразуем в DTO
        return mapToResponseDto(savedReview);
    }

    public List<ReviewResponseDto> getReviewsByUser(Long userId) {
        // Проверяем, существует ли пользователь
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Review> reviews = reviewRepository.findByTargetId(userId);
        return reviews.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Double getAverageRating(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<Review> reviews = reviewRepository.findByTargetId(userId);
        if (reviews.isEmpty()) {
            return 0.0;
        }

        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }

    @Transactional
    public void deleteReview(Long reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));

        // Проверяем, что удаляет автор или админ
        if (!review.getAuthor().getEmail().equals(userEmail)) {
            throw new RuntimeException("Вы можете удалить только свой отзыв");
        }

        reviewRepository.delete(review);
    }

    private ReviewResponseDto mapToResponseDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setText(review.getText());
        dto.setAuthorEmail(review.getAuthor().getEmail());
        dto.setAuthorFullName(review.getAuthor().getFullName());
        dto.setTargetEmail(review.getTarget().getEmail());
        dto.setTargetFullName(review.getTarget().getFullName());
        return dto;
    }
}
