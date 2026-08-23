package com.travelbuddy.service;

import com.travelbuddy.dto.CommentRequestDto;
import com.travelbuddy.dto.CommentResponseDto;
import com.travelbuddy.entity.Comment;
import com.travelbuddy.entity.Trip;
import com.travelbuddy.entity.User;
import com.travelbuddy.repository.CommentRepository;
import com.travelbuddy.repository.TripRepository;
import com.travelbuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Trip trip;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("user@buddy.by");
        user.setFullName("Test User");

        trip = new Trip();
        trip.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setMessage("Hi");
        comment.setMessageAuthor(user);
        comment.setTrip(trip);
    }

    @Test
    void addComment_ShouldSave() {
        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(userRepository.findByEmail("user@buddy.by")).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentResponseDto response = commentService.addComment(1L, "user@buddy.by", new CommentRequestDto("Hi"));
        assertNotNull(response);
        assertEquals("Hi", response.getMessage());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_ShouldThrow_WhenTripNotFound() {
        when(tripRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> commentService.addComment(999L, "user@buddy.by", new CommentRequestDto("Hi")));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteComment_ShouldDelete_WhenAuthor() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, "user@buddy.by");
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_ShouldThrow_WhenNotAuthor() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        assertThrows(RuntimeException.class, () -> commentService.deleteComment(1L, "other@buddy.by"));
        verify(commentRepository, never()).delete(any(Comment.class));
    }

    @Test
    void deleteComment_ShouldThrow_WhenCommentNotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentService.deleteComment(999L, "user@buddy.by"));
        verify(commentRepository, never()).delete(any(Comment.class));
    }
}
