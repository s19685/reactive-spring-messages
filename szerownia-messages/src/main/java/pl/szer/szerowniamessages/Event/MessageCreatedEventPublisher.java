package pl.szer.szerowniamessages.Event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

@Component
public class MessageCreatedEventPublisher implements
        ApplicationListener<MessageCreatedEvent>,
        Consumer<FluxSink<MessageCreatedEvent>> {

    private final Executor executor;
    private final BlockingQueue<MessageCreatedEvent> queue =
            new LinkedBlockingQueue<>();

    MessageCreatedEventPublisher(Executor executor) {
        this.executor = executor;
    }


    @Override
    public void onApplicationEvent(MessageCreatedEvent event) {
        this.queue.offer(event);
    }

    @Override
    public void accept(FluxSink<MessageCreatedEvent> sink) {
        this.executor.execute(() -> {
            while (true)
                try {
                    MessageCreatedEvent event = queue.take();
                    sink.next(event);
                }
                catch (InterruptedException e) {
                    ReflectionUtils.rethrowRuntimeException(e);
                }
        });
    }
}