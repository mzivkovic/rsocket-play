package com.mzivkovic.flow.processes;

import com.mzivkovic.flow.domain.Serialization;
import com.mzivkovic.flow.domain.UserStore;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

import static com.mzivkovic.flow.Ports.DB_PORT;

public class DB {

    public static final int USER_COUNT = 100_000;
    public final UserStore userStore;
    private final int port;

    public DB(UserStore userStore, int port) {
        this.userStore = userStore;
        this.port = port;
    }

    public void start() {
        RSocketFactory.receive()
                .acceptor((setup, sending) -> Mono.just(new DepartmentHandler(userStore)))
                .transport(TcpServerTransport.create(port))
                .start()
                .block();
    }

    private static class DepartmentHandler extends AbstractRSocket {
        public final UserStore userStore;

        private DepartmentHandler(UserStore userStore) {
            this.userStore = userStore;
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            return userStore.departments()
                    .map(Serialization::to);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        UserStore userStore = new UserStore();
        userStore.init(USER_COUNT);
        DB db = new DB(userStore, DB_PORT);
        db.start();
        Thread.sleep(TimeUnit.HOURS.toMillis(2));
    }

}
