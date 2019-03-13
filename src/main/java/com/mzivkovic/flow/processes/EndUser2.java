package com.mzivkovic.flow.processes;

import com.mzivkovic.flow.domain.Serialization;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

import java.util.concurrent.TimeUnit;

import static com.mzivkovic.flow.Ports.SERVER_PORT;

public class EndUser2 {
    public static void main(String[] args) throws InterruptedException {
        RSocket socket = RSocketFactory.connect()
                .errorConsumer(System.out::println)
                .transport(TcpClientTransport.create(SERVER_PORT))
                .start()
                .block();

        socket.requestStream(DefaultPayload.create("bla"))
                .subscribe(new BaseSubscriber<>() {

                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Payload payload) {
                        System.out.println(Serialization.getEmployee(payload));
                        request(1000);
                    }
                });

        Thread.sleep(TimeUnit.HOURS.toMillis(2));
    }
}
