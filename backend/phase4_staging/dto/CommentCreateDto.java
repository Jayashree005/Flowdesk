package com.flowdesk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommentCreateDto {

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotBlank(message = "Comment content cannot be blank")
    private String content;

    public CommentCreateDto() {}

    public CommentCreateDto(Long authorId, String content) {
        this.authorId = authorId;
        this.content = content;
    }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
