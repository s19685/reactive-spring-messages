package pl.szer.szerowniamessages.Models;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class Thread {
    private String id;
    private String authorId;
    private String receiverId;
    private Long adId;
    private Long ticketId;
    private Flux<Message> messages = Flux.empty();
    private LocalDateTime lastMessageTime;

    public Thread() {
    }

    public Thread(String authorId, String receiverId, Long adId, Long ticketId) {
        this.authorId = authorId;
        this.receiverId = receiverId;
        this.adId = adId;
        this.ticketId = ticketId;
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

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public Long getTicketID() {
        return ticketId;
    }

    public void setTicketID(Long ticketID) {
        this.ticketId = ticketID;
    }

    public Flux<Message> getMessages() {
        return messages;
    }

    public Flux<Message> setMessages(Flux<Message> messages) {
        this.messages = messages;
        return this.messages;
    }

    public String getSubject() {
        return adId == null ? "TEMAT TICKET" : "TEMAT OGLOSZNIE";
    }

    public String getLastMessage() {
        Message message = messages.blockLast();
        if (message == null) return "EMPTY";

        return message.getMessage();
    }

    public Long getUnreaded() {
        return messages.filter(message -> !message.isSeen()).count().block();
    }

    public void addMessage(Message message) {
        messages = Flux.concat(messages, Mono.just(message));
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }
}
