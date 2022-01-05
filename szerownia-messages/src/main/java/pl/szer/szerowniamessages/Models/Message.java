package pl.szer.szerowniamessages.Models;

import java.time.LocalDateTime;

public class Message {
    private String id;
    private String authorId;
    private String message;
    private LocalDateTime createdDate;
    private LocalDateTime seenDate;

    private String threadId;

    public Message() {
    }

    public Message(String id, String message) {
        this.id = id;
        this.message = message;
        createdDate = LocalDateTime.now();
    }

    public Message(String authorId, String message, String threadId) {
        this.authorId = authorId;
        this.message = message;
        this.threadId = threadId;
        createdDate = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getThread() {
        return threadId;
    }

    public void setThread(String threadId) {
        this.threadId = threadId;
    }

    public Message setSeenDate() {
        this.seenDate = LocalDateTime.now();
        return this;
    }

    public LocalDateTime getSeenDate() {
        return seenDate;
    }

    public boolean isSeen() {
        return this.seenDate != null;
    }
}
