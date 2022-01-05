package pl.szer.szerowniamessages;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class MessengerController {

    @Bean
    RouterFunction<ServerResponse> messagesEndpoints(MessengerHandler handler) {
        return route()

                .GET("/threads", handler::myThreads)

                .GET("/thread/{id}", handler::myThread)

                .POST("/message/{id}", handler::sendMessage)

                .POST("/newMessage/{id}", handler::sendNewMessage)

                .build();
    }
}