package com.flowdesk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Comment() {}

    public Comment(Long id, Ticket ticket, User user, String content, LocalDateTime createdAt) {
        this.id = id;
        this.ticket = ticket;
        this.user = user;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Comment(Ticket ticket, User user, String content) {
        this.ticket = ticket;
        this.user = user;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static CommentBuilder builder() {
        return new CommentBuilder();
    }

    public static class CommentBuilder {
        private Long id;
        private Ticket ticket;
        private User user;
        private String content;
        private LocalDateTime createdAt;

        public CommentBuilder id(Long id) { this.id = id; return this; }
        public CommentBuilder ticket(Ticket ticket) { this.ticket = ticket; return this; }
        public CommentBuilder user(User user) { this.user = user; return this; }
        public CommentBuilder content(String content) { this.content = content; return this; }
        public CommentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Comment build() {
            return new Comment(id, ticket, user, content, createdAt != null ? createdAt : LocalDateTime.now());
        }
    }
}
