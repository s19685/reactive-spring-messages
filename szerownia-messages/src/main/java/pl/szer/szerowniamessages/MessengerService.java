package pl.szer.szerowniamessages;

import pl.szer.szerowniamessages.Models.*;
import pl.szer.szerowniamessages.Models.ThreadDTO;

import pl.szer.szerowniamessages.Models.Thread;
import pl.szer.szerowniamessages.Event.MessageCreatedEvent;
import org.reactivestreams.Publisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.function.Function;

import static pl.szer.szerowniamessages.Config.JwtTokenUtil.loggedId;

@Service
public class MessengerService {

    private MessageRepo messageRepo;
    private ThreadRepo threadRepo;
    private ApplicationEventPublisher publisher;

    public MessengerService(MessageRepo messageRepo, ThreadRepo threadRepo, ApplicationEventPublisher publisher) {
        this.messageRepo = messageRepo;
        this.threadRepo = threadRepo;
        this.publisher = publisher;
    }

    Flux<ThreadDTO> getMyThreads(String auth) {
        String id = loggedId(auth);

        return threadRepo
                .findAllByUserOrderByLastMessageTime(id)
                .concatMap(countNew(messageRepo, id))
                .map(toThreadDTO());
    }

    Flux<Message> getMessages(String id, String auth) {
        return messageRepo
                .findAllByThreadIdOrderByCreatedDate(id)
                .map(setSeenDate(loggedId(auth)))
                .concatMap(messageRepo::save);
    }

    Mono<Thread> sendMessage(String threadId, Mono<String> message, String auth) {
        return send(threadId, message, loggedId(auth));
    }

    Mono<Thread> sendNewMessage(String threadDetails, Mono<String> message, String auth) {
        ThreadDetails td = new ThreadDetails(threadDetails);
        String logged = loggedId(auth);

        return threadRepo
                .findByDetails(logged, td.getReceiver(), td.getVal())
                .switchIfEmpty(newThread(td.isAboutAd(), logged, td.getReceiver(), td.getVal()))
                .flatMap(sendToThread(message, logged));
    }

    private Function<Thread, Mono<? extends Thread>> sendToThread(Mono<String> message, String logged) {
        return t -> send(t.getId(), message, logged);
    }

    private Mono<Thread> send(String id, Mono<String> body, String logged) {
        Mono<Message> message = getMessage(id, body, logged);

        return threadRepo
                .findById(id)
                .flatMap(t -> Mono.zip(Mono.just(t), message))
                .map(addMessageToThread())
                .flatMap(threadRepo::save)
                .doOnSuccess(t -> publisher.publishEvent(new MessageCreatedEvent(t)));
    }

    private Mono<Message> getMessage(String id, Mono<String> body, String logged) {
        return body
                .map(m -> new Message(logged, m, id))
                .flatMap(messageRepo::save);
    }

    private Mono<Thread> newThread(boolean isAboutAd, String logged, String receiver, Long val) {
        Thread thread = isAboutAd ? new Thread(logged, receiver, val, null)
                                  : new Thread(logged, receiver, null, val);
        return threadRepo.save(thread);
    }

    private Function<Tuple2<Thread, Long>, ThreadDTO> toThreadDTO() {
        return tuple -> {
            Thread t = tuple.getT1();
            ThreadDTO dto = new ThreadDTO(t.getId(), t.getAuthorId(), t.getReceiverId(),
                    t.getSubject(), t.getLastMessage(), tuple.getT2(), t.getLastMessageTime());
            if (t.getAdId() == null)
                dto.setTicketId(t.getTicketID());
            else
                dto.setAdId(t.getAdId());
            return dto;
        };
    }

    private Function<Tuple2<Thread, Message>, Thread> addMessageToThread() {
        return tuple -> {
            tuple.getT1().addMessage(tuple.getT2());
            tuple.getT1().setLastMessageTime(tuple.getT2().getCreatedDate());
            return tuple.getT1();
        };
    }

    private Function<Message, Message> setSeenDate(String id) {
        return m -> {
            if (!m.isSeen() && !m.getAuthorId().equals(id))
                m.setSeenDate();
            return m;
        };
    }

    private Function<Thread, Publisher<? extends Tuple2<Thread, Long>>> countNew(MessageRepo mr, String id) {
        return t -> Flux.zip(Mono.just(t), mr.countAllByThreadIdAndSeenDateIsNullAndAuthorIdIsNot(t.getId(), id));
    }
}