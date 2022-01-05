package pl.szer.szerowniamessages.Models;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface MessageRepo extends ReactiveCrudRepository<Message,String> {

    Flux<Message> findAllByThreadIdOrderByCreatedDate(String id);

    Mono<Long> countAllByThreadIdAndSeenDateIsNullAndAuthorIdIsNot(String threadId, String  authorId);

}
