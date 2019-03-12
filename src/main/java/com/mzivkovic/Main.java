package com.mzivkovic;

import com.mzivkovic.domain.dataStore.DepartmentDataStore;
import com.mzivkovic.domain.serialization.Serialization;
import com.mzivkovic.services.DepartmentService;
import io.rsocket.RSocket;
import io.rsocket.RSocketFactory;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.util.DefaultPayload;

import static com.mzivkovic.services.Ports.DEPARTMENT_PORT;

public class Main {
    public static void main(String[] args) {
        DepartmentDataStore departmentDataStore = new DepartmentDataStore();
        departmentDataStore.init(1_00_000);
        DepartmentService departmentService = new DepartmentService(departmentDataStore, DEPARTMENT_PORT);

        departmentService.start();

        RSocket socket = RSocketFactory.connect()
                .errorConsumer(System.out::println)
                .transport(TcpClientTransport.create(DEPARTMENT_PORT))
                .start()
                .block();

        socket.requestStream(DefaultPayload.create("bla"))
                .map(Serialization::departmentFromBuffer)
                .blockLast();
    }

}
