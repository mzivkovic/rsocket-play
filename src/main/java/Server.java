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
import reactor.core.publisher.UnicastProcessor;

import java.util.concurrent.ConcurrentLinkedDeque;

public class Server {

    public static final int PORT = 8082;

    private static ConcurrentLinkedDeque<String> deque = new ConcurrentLinkedDeque<>();

    public static void main(String[] args) {

        deque.add("Milan");
        UnicastProcessor<String> strings = UnicastProcessor.create(deque);

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
        private ConcurrentLinkedDeque<String> deque;

        public DefaultSimpleService(RSocket client, ConcurrentLinkedDeque<String> deque) {
            this.client = client;
            this.deque = deque;
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            return UnicastProcessor.create(deque)
                    .map(s -> DefaultPayload.create(String.valueOf(s)));

//            return Mono.just(Integer.valueOf(payload.getDataUtf8()))
//                    .doOnNext(i -> System.out.println("Server received:" + i))
//                    .flatMapMany(i -> Flux.range(0, i))
//                    .doOnNext(i -> System.out.println("Sending:" + i))
//                    .map(i -> DefaultPayload.create(String.valueOf(i)));

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
