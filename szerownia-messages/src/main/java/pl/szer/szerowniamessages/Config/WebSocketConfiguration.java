package pl.szer.szerowniamessages.Config;

import org.bson.BsonDocument;
import org.bson.json.JsonObject;
import pl.szer.szerowniamessages.Event.MessageCreatedEvent;
import pl.szer.szerowniamessages.Event.MessageCreatedEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static pl.szer.szerowniamessages.Config.JwtTokenUtil.loggedId;

@Log4j2
@Configuration
class WebSocketConfiguration {

    @Bean
    Executor executor() {
        return Executors.newSingleThreadExecutor();
    }

    @Bean
    HandlerMapping handlerMapping(WebSocketHandler wsh) {
        return new SimpleUrlHandlerMapping() {
            {
                setUrlMap(Collections.singletonMap("/ws/messages", wsh));

                setOrder(10);
            }
        };
    }

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler(ObjectMapper objectMapper, MessageCreatedEventPublisher eventPublisher) {

        Flux<MessageCreatedEvent> publish = Flux
                .create(eventPublisher)
                .share();

        return session -> {
            Flux<WebSocketMessage> messageFlux = publish
                    .map(evt -> {
                        try {
                            return objectMapper.writeValueAsString(evt.getSource());
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .map(str -> {
                        String token, id, authorId, receiverId;
                        BsonDocument jo = new JsonObject(str).toBsonDocument();

                        token = session.getHandshakeInfo().getUri().getQuery().substring(3);

                        id = loggedId("Bearer " + token);
                        authorId = jo.get("authorId").asString().getValue();
                        receiverId = jo.get("receiverId").asString().getValue();

                        if ( !id.equals(authorId) && !id.equals(receiverId))
                            return session.textMessage("XD");

                        log.info("sending " + str);
                        return session.textMessage(str);
                    })
                    .filter(m -> !m.getPayloadAsText().equals("XD"));

            return session.send(messageFlux);
        };
    }
}