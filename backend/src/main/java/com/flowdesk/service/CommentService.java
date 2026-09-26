package com.flowdesk.service;

import com.flowdesk.dto.AddCommentRequest;
import com.flowdesk.dto.CommentResponse;
import com.flowdesk.entity.Comment;
import com.flowdesk.entity.Ticket;
import com.flowdesk.entity.User;
import com.flowdesk.exception.ResourceNotFoundException;
import com.flowdesk.repository.CommentRepository;
import com.flowdesk.repository.TicketRepository;
import com.flowdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, TicketRepository ticketRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentResponse addComment(
            Long ticketId,
            AddCommentRequest request
    ) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket not found: " + ticketId
                        ));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + request.getUserId()
                        ));

        Comment comment = Comment.builder()
                .ticket(ticket)
                .user(user)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        return toResponse(comment);
    }

    @Transactional
    public CommentResponse addComment(Long ticketId, Long userId, String content) {
        AddCommentRequest req = new AddCommentRequest();
        req.setUserId(userId);
        req.setContent(content);
        return addComment(ticketId, req);
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long ticketId) {

        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException(
                    "Ticket not found: " + ticketId
            );
        }

        return commentRepository
                .findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CommentResponse toResponse(Comment comment) {

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .userName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
