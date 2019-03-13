package com.mzivkovic.talk;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mzivkovic.talk.BatMan.getLines;

public class SpiderMan {

    public static void main ( String[] args ) throws Exception {
        final RSocket spiderMan = startSpiderMan();

        spiderMan.requestStream( DefaultPayload.create( "3" ) )
                .map( Payload::getDataUtf8 )
                .blockLast();

        Thread.sleep( TimeUnit.HOURS.toMillis( 2 ) );
    }


    private static RSocket startSpiderMan () throws IOException, URISyntaxException {
        final List<String> lines = getLines( "/spiderman.txt" );

        return RSocketFactory.connect()
                .acceptor(s ->  new SpeakListenAcceptor( "Spider-Man", s, lines ))
                .transport( TcpClientTransport.create( BatMan.PORT ))
                .start()
                .block();

    }
}
