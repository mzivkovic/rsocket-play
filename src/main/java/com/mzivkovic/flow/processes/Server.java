package com.mzivkovic.flow.processes;

import com.mzivkovic.flow.domain.Employee;
import com.mzivkovic.flow.domain.Serialization;
import com.mzivkovic.flow.domain.User;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.mzivkovic.flow.Ports.DB_PORT;
import static com.mzivkovic.flow.Ports.SERVER_PORT;

public class Server {
    private final int port;
    private int dbPort;
    private volatile RSocket dbSocket;

    public Server(int port, int dbPort) {
        this.dbPort = dbPort;
        this.port = port;
    }

    public void start() {
        dbSocket = RSocketFactory.connect()
                .errorConsumer(System.out::println)
                .transport(TcpClientTransport.create(dbPort))
                .start()
                .block();

        RSocketFactory.receive()
                .acceptor((setup, sending) -> Mono.just(new Handler(dbSocket)))
                .transport(TcpServerTransport.create(port))
                .start()
                .block();
    }

    private static class Handler extends AbstractRSocket {

        private RSocket dbSocket;

        public Handler(RSocket dbSocket) {
            this.dbSocket = dbSocket;
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            return dbSocket.requestStream(DefaultPayload.create("get_me_data"))
                    .map(Serialization::getUser)
                    .map(this::addDepartment)
                    .map(Serialization::to);
        }

        private Employee addDepartment(User user) {
            return new Employee(user, ThreadLocalRandom.current().nextInt(100));
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Server server = new Server(SERVER_PORT, DB_PORT);
        server.start();
        Thread.sleep(TimeUnit.HOURS.toMillis(2));
    }
}
