package com.mzivkovic.services;

import com.mzivkovic.domain.dataStore.EmploymentDataStore;
import com.mzivkovic.domain.serialization.Serialization;
import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocketFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mzivkovic.transport.TransportUtils.createTransport;

public class EmploymentService {
    public final EmploymentDataStore employmentDataStore;
    private final int port;

    public EmploymentService(EmploymentDataStore employmentDataStore, int port) {
        this.employmentDataStore = employmentDataStore;
        this.port = port;
    }

    public void start() {
        RSocketFactory.receive()
                .acceptor((setup, sending) -> Mono.just(new EmployeeHandler(employmentDataStore)))
                .transport(createTransport(port))
                .start()
                .block();
    }

    private static class EmployeeHandler extends AbstractRSocket {
        public final EmploymentDataStore employeeDataStore;

        private EmployeeHandler(EmploymentDataStore employeeDataStore) {
            this.employeeDataStore = employeeDataStore;
        }

        @Override
        public Flux<Payload> requestStream(Payload payload) {
            return employeeDataStore.departments()
                    .map(Serialization::toBuffer);
        }

        @Override
        public Mono<Void> fireAndForget(Payload payload) {
            employeeDataStore.put(Serialization.employeeFromBuffer(payload));
            return Mono.empty();
        }
    }
}
