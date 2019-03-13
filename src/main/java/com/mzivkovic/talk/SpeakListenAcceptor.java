package com.mzivkovic.talk;

import io.rsocket.AbstractRSocket;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

class SpeakListenAcceptor extends AbstractRSocket {

    private static final int DELAY = 2;
    private final String name;
    private final RSocket other;
    private final List<String> text;
    private volatile int position;

    SpeakListenAcceptor ( String name, RSocket other, List<String> text ) {
        this.name = name;
        this.other = other;
        this.text = text;
//        other.requestStream(  )
    }


    @Override
    public Flux<Payload> requestStream ( Payload payload ) {
        System.out.println( name + " ****" );
        return getText( payload )
                .delayElements( Duration.ofSeconds( DELAY ) )
                .doOnNext( this::speak )
                .map( DefaultPayload::create )
                .doOnComplete( this::askToSpeak );
    }


    private void askToSpeak () {
        final int speakLimit = ThreadLocalRandom.current().nextInt( 4 ) + 1;
        other.requestStream( DefaultPayload.create( String.valueOf( speakLimit ) ) )
                .map( Payload::getDataUtf8 )
                .subscribe();
//                .doOnNext( this::listen );
    }

    private void speak ( final String line ) {
        System.out.println( line );
    }


    private void listen ( final String line ) {
        System.out.println( name + " : " + line );
    }


    private Flux<String> getText ( final Payload payload ) {
        final int size = Integer.parseInt( payload.getDataUtf8() );
        int to = position + size > text.size() ? text.size() : position + size;
        if ( position > text.size() ) {
            position = 0;
        }
        int currentPosition = position;
        position += size;
        return Flux.fromIterable( text.subList( currentPosition, to ) );
    }

    public void startConversation ( final int batManPort ) {

    }
}
