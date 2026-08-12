package com.travelbuddy.controller;

import com.travelbuddy.dto.CommentRequestDto;
import com.travelbuddy.dto.CommentResponseDto;
import com.travelbuddy.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponseDto addComment(
            @PathVariable Long tripId,
            @RequestBody CommentRequestDto requestDto,
            Principal principal) {
        String userEmail = principal.getName();
        return commentService.addComment(tripId, userEmail, requestDto);
    }

    @GetMapping
    public List<CommentResponseDto> getComments(@PathVariable Long tripId) {
        return commentService.getCommentsByTrip(tripId);
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment(
            @PathVariable Long commentId,
            Principal principal) {
        String userEmail = principal.getName();
        commentService.deleteComment(commentId, userEmail);
        return "Комментарий удален";
    }
}
