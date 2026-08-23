package com.travelbuddy.service;

import com.travelbuddy.dto.CommentRequestDto;
import com.travelbuddy.dto.CommentResponseDto;
import com.travelbuddy.entity.Comment;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.CommentRepository;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final ApplicationService applicationService;

    @Transactional
    public CommentResponseDto addComment(Long tripId, String authorEmail, CommentRequestDto requestDto) {

        // --- НОВАЯ ПРОВЕРКА: ТОЛЬКО УЧАСТНИКИ ПОЕЗДКИ МОГУТ ПИСАТЬ ---
        if (!applicationService.isUserParticipant(tripId, authorEmail)) {
            throw new RuntimeException("Только участники поездки могут оставлять комментарии");
        }

        // 1. Находим поездку
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));

        // 2. Находим автора
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // 3. Создаем комментарий
        Comment comment = new Comment();
        comment.setMessage(requestDto.getMessage());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setTrip(trip);
        comment.setMessageAuthor(author);

        Comment savedComment = commentRepository.save(comment);

        // 4. Преобразуем в DTO
        return mapToResponseDto(savedComment);
    }

    public List<CommentResponseDto> getCommentsByTrip(Long tripId) {
        // Проверяем, существует ли поездка
        tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Поездка не найдена"));

        // Создаем объект сортировки: по полю createdAt, убывание (DESC)
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");

        // Получаем комментарии с сортировкой (указываем, что ищем по tripId)
        List<Comment> comments = commentRepository.findByTripId(tripId, sort);

        return comments.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        // Проверяем, что удаляет автор или админ
        if (!comment.getMessageAuthor().getEmail().equals(userEmail)) {
            throw new RuntimeException("Вы можете удалить только свой комментарий");
        }

        commentRepository.delete(comment);
    }

    private CommentResponseDto mapToResponseDto(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setMessage(comment.getMessage());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setAuthorEmail(comment.getMessageAuthor().getEmail());
        dto.setAuthorFullName(comment.getMessageAuthor().getFullName());
        return dto;
    }
}
