package pl.szer.szerowniamessages.Event;

import pl.szer.szerowniamessages.Models.Thread;
import org.springframework.context.ApplicationEvent;

public class MessageCreatedEvent extends ApplicationEvent {
    public MessageCreatedEvent(Thread thread) {
        super(thread);
    }
}
