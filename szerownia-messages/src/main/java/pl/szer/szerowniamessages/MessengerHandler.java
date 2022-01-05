package pl.szer.szerowniamessages;

import pl.szer.szerowniamessages.Models.ThreadDTO;
import pl.szer.szerowniamessages.Models.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class MessengerHandler {

    private MessengerService ms;

    public MessengerHandler(MessengerService ms) {
        this.ms = ms;
    }

    Mono<ServerResponse> myThreads(ServerRequest r) {
        return ok().body(ms.getMyThreads(header(r)), ThreadDTO.class)
                .switchIfEmpty(
               notFound().build());
    }

    Mono<ServerResponse> myThread(ServerRequest r) {
        return ok().body(ms.getMessages(pathId(r), header(r)), Message.class);
    }

    Mono<ServerResponse> sendMessage(ServerRequest r) {
        return ok().body(ms.sendMessage(pathId(r), body(r), header(r)), Thread.class);
    }

    Mono<ServerResponse> sendNewMessage(ServerRequest r) {
        return ok().body(ms.sendNewMessage(pathId(r), body(r), header(r)), Message.class);
    }


    private Mono<String> body(ServerRequest request) {
        return request.bodyToMono(String.class);
    }

    private String header(ServerRequest r) {
        return r.headers().firstHeader("Authorization");
    }

    private String pathId(ServerRequest request) {
        return request.pathVariable("id");
    }
}
