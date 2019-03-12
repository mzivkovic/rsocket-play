import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Client {

    public static void main(String[] args) throws InterruptedException {

        //CLIENT
        RSocket socket = RSocketFactory.connect()
                .acceptor(comm -> new ClientAcceptor())
                .transport(TcpClientTransport.create(Server.PORT))
                .start()
                .block();


        new Thread(() -> Flux.range(3, 100)
                .delayElements(Duration.ofSeconds(2))
                .doOnNext(i -> System.out.println("Sending: " + i))
                .map(i -> DefaultPayload.create(String.valueOf(i)))
                .flatMap(socket::fireAndForget)
                .blockLast()).start();

        new Thread(() -> {
            System.out.println("Starting");
            socket.requestStream(DefaultPayload.create("hey"))
                    .doOnNext(response -> System.out.println(response.getDataUtf8()))
                    .doOnComplete(() -> System.out.println("Completed"))
                    .blockLast();
        })
                .start();

        Thread.sleep(1000_000);
    }


    private static class ClientAcceptor extends AbstractRSocket {
        @Override
        public Flux<Payload> requestStream(Payload payload) {
            return super.requestStream(payload);
        }
    }
}
