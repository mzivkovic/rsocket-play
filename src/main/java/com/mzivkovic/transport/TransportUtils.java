package com.mzivkovic.transport;

import io.rsocket.transport.ServerTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;

public class TransportUtils {
    public static ServerTransport createTransport(int port) {
        return TcpServerTransport.create(port);
    }
}
