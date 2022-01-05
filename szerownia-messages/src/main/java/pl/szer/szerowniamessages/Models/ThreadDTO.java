package pl.szer.szerowniamessages.Models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ThreadDTO {
    String id, authorId, receiverId, subject, lastMessage;
    LocalDateTime lastMessageTime;
    Long unreaded, ticketId, adId;

    public ThreadDTO(String id, String authorId, String receiverId, String subject,
                     String lastMessage, Long unreaded, LocalDateTime lastMessageTime) {

        this.id = id;
        this.authorId = authorId;
        this.receiverId = receiverId;
        this.subject = subject;
        this.lastMessage = lastMessage;
        this.unreaded = unreaded;
        this.lastMessageTime = lastMessageTime;
    }
}
