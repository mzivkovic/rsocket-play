package com.mzivkovic.flow.processes;

import com.mzivkovic.flow.domain.Serialization;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;

import java.time.Duration;

import static com.mzivkovic.flow.Ports.SERVER_PORT;

public class EndUser {

    public static void main(String[] args) {
        RSocket socket = RSocketFactory.connect()
                .errorConsumer(System.out::println)
                .transport(TcpClientTransport.create(SERVER_PORT))
                .start()
                .block();

        socket.requestStream(DefaultPayload.create("bla"))
                .delayElements(Duration.ofSeconds(1))
                .map(Serialization::getEmployee)
                .doOnNext(e -> System.out.println(e.getUser().getName()))
                .blockLast();
    }
}
