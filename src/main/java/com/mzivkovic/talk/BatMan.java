package com.mzivkovic.talk;

import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BatMan {
    public static final int PORT = 7082;

    public static void main(String[] args) throws Exception {
        final List<String> lines = getLines( "/batman.txt" );
        RSocketFactory.receive()
                .acceptor( ( setup, socket ) -> Mono.just( new SpeakListenAcceptor( "Bat-Man", socket, lines ) ) )
                .transport( TcpServerTransport.create( PORT ) )
                .start()
                .block();
        Thread.sleep( TimeUnit.HOURS.toMillis( 2 ) );
    }

    public static List<String> getLines ( String file ) throws IOException, URISyntaxException {
        return Files.readAllLines(
                Paths.get( BatMan.class.getResource( file ).toURI() ), StandardCharsets.UTF_8 );
    }
}
