package com.flowdesk.dto;

import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    private Long ticketId;
    private UserDto author;
    private String content;
    private LocalDateTime createdAt;

    public CommentDto() {}

    public CommentDto(Long id, Long ticketId, UserDto author, String content, LocalDateTime createdAt) {
        this.id = id;
        this.ticketId = ticketId;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public UserDto getAuthor() { return author; }
    public void setAuthor(UserDto author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
