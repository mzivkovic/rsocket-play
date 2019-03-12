package com.mzivkovic.services;

import com.mzivkovic.domain.dataStore.DepartmentDataStore;
import com.mzivkovic.domain.serialization.Serialization;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mzivkovic.transport.TransportUtils.createTransport;

public class DepartmentService {

    public final DepartmentDataStore departmentDataStore;
    private final int port;

    public DepartmentService(DepartmentDataStore departmentDataStore, int port) {
        this.departmentDataStore = departmentDataStore;
        this.port = port;
    }

    public void start() {
        RSocketFactory.receive()
                .acceptor((setup, sending) -> Mono.just(new DepartmentHandler(departmentDataStore)))
                .transport(createTransport(port))
                .start()
                .block();
    }

    private static class DepartmentHandler extends AbstractRSocket {
        public final DepartmentDataStore departmentDataStore;

        private DepartmentHandler(DepartmentDataStore departmentDataStore) {
            this.departmentDataStore = departmentDataStore;
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            return departmentDataStore.departments()
                    .map(Serialization::toBuffer);
        }

        @Override
        public Mono<Void> fireAndForget(Payload payload) {
            departmentDataStore.put(Serialization.departmentFromBuffer(payload));
            return Mono.empty();
        }
    }
}
