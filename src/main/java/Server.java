import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.LinkedBlockingDeque;

public class Server {

    public static final int PORT = 8082;

    private static LinkedBlockingDeque<String> deque = new LinkedBlockingDeque<>();

    public static void main(String[] args) {

        deque.add("Milan");


        //SERVER
        RSocketFactory.receive()
                .acceptor((setup, socket) ->
                        Mono.just(new DefaultSimpleService(socket, deque)))
                .transport(TcpServerTransport.create(PORT))
                .start()
                .block()
                .onClose()
                .block();
    }

    private static class DefaultSimpleService extends AbstractRSocket {
        private RSocket client;
        private LinkedBlockingDeque<String> deque;
        private final Flux<String> flux;

        public DefaultSimpleService(RSocket client, LinkedBlockingDeque<String> deque) {
            this.client = client;
            this.deque = deque;

            flux = Flux.<String>create(sink -> {
                try {
                    while (true) {
                        sink.next(deque.takeLast());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).subscribeOn(Schedulers.elastic());
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            return flux
                    .map(s -> DefaultPayload.create(String.valueOf(s)));
        }

        @Override
        public Mono<Void> fireAndForget(Payload payload) {
            deque.add(payload.getDataUtf8());
            return Mono.empty();
        }

        @Override
        public Flux<Payload> requestChannel(Publisher<Payload> payloads) {
            payloads.subscribe(new ClientListener());
            return Flux.empty();
        }
    }

    private static class ClientListener extends BaseSubscriber<Payload> {

        @Override
        protected void hookOnSubscribe(Subscription subscription) {
            subscription.request(1);
        }

        @Override
        protected void hookOnNext(Payload value) {
            System.out.println(value.getDataUtf8());
            request(1);
        }
    }

}
