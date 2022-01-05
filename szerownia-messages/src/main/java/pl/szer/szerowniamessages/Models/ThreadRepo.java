package pl.szer.szerowniamessages.Models;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ThreadRepo extends ReactiveCrudRepository<Thread, String> {

    @Query("{ $or : [{ 'authorId' : ?0 }, { 'receiverId' : ?0 }] }")
    Flux<Thread> findAllByUserOrderByLastMessageTime(String id);

    @Query("{ $or : [{ 'authorId' : ?0, 'receiverId' : ?1, 'ticketId' : ?2 }," +
            "        { 'authorId' : ?0, 'receiverId' : ?1, 'adId' : ?2 }] }")
    Mono<Thread> findByDetails(String authorId,  String receiverId, Long val);

}
