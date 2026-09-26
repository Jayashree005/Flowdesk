package com.flowdesk.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    private String userName;
    private LocalDateTime createdAt;

    public CommentResponse() {}

    public CommentResponse(Long id, String content, String userName, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.userName = userName;
        this.createdAt = createdAt;
    }

    public static CommentResponseBuilder builder() {
        return new CommentResponseBuilder();
    }

    public static class CommentResponseBuilder {
        private Long id;
        private String content;
        private String userName;
        private LocalDateTime createdAt;

        public CommentResponseBuilder id(Long id) { this.id = id; return this; }
        public CommentResponseBuilder content(String content) { this.content = content; return this; }
        public CommentResponseBuilder userName(String userName) { this.userName = userName; return this; }
        public CommentResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public CommentResponse build() {
            return new CommentResponse(id, content, userName, createdAt);
        }
    }

    public Long getId() { return id; }
    public String getContent() { return content; }
    public String getUserName() { return userName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
