package com.mzivkovic.talk;

import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Program {

    public static final int PORT = 7082;

    public static void main ( String[] args ) throws Exception {
        startBatman();
        final RSocket spiderMan = startSpiderMan();
        startConversation(spiderMan);

        Thread.sleep( TimeUnit.MINUTES.toMillis( 30 ) );

    }

    private static void startConversation ( final RSocket spiderMan ) {
        spiderMan.requestStream( DefaultPayload.create( "3" ) )
                .map( Payload::getDataUtf8 )
                .blockLast();
    }

    private static void startBatman () throws IOException, URISyntaxException {
        final List<String> lines = getLines( "/batman.txt" );
        RSocketFactory.receive()
                .acceptor( ( setup, socket ) -> Mono.just( new SpeakListenAcceptor( "Bat-Man", socket, lines ) ) )
                .transport( TcpServerTransport.create( PORT ) )
                .start()
                .block();
    }

    private static RSocket startSpiderMan () throws IOException, URISyntaxException {
        final List<String> lines = getLines( "/spiderman.txt" );

        return RSocketFactory.connect()
                .acceptor(s ->  new SpeakListenAcceptor( "Spider-Man", s, lines ))
                .transport( TcpClientTransport.create( PORT ))
                .start()
                .block();

    }

    private static List<String> getLines ( String file ) throws IOException, URISyntaxException {
        return Files.readAllLines(
                Paths.get( Program.class.getResource( file ).toURI() ), StandardCharsets.UTF_8 );
    }

}
