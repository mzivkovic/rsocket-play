package com.mzivkovic.flow.domain;

import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Serialization {

    public static Payload to(User user) {

        byte[] name = user.getName().getBytes(StandardCharsets.UTF_8);
        int bufferSize = 8 + 4 + name.length;
        ByteBuffer buffer = serializeUser(user, name, bufferSize);
        buffer.flip();

        return DefaultPayload.create(buffer);
    }


    public static Payload to(Employee employee) {

        User user = employee.getUser();
        byte[] nameBytes = user.getName().getBytes(StandardCharsets.UTF_8);
        int bufferSize = 8 + 4 + nameBytes.length + 8;

        ByteBuffer buffer = serializeUser(user, nameBytes, bufferSize);
        buffer.putLong(employee.getDepartmentId());
        buffer.flip();

        return DefaultPayload.create(buffer);
    }

    public static User getUser(Payload payload) {
        ByteBuffer buffer = payload.getData();
        return getUser(buffer);
    }


    public static Employee getEmployee(Payload payload) {
        ByteBuffer buffer = payload.getData();
        User user = getUser(buffer);
        long departmentId = buffer.getLong();
        return new Employee(user, departmentId);
    }

    private static ByteBuffer serializeUser(User user, byte[] nameBytes, int bufferSize) {
        ByteBuffer buffer1 = ByteBuffer.allocate(bufferSize);
        buffer1.putLong(user.getId());
        buffer1.putInt(nameBytes.length);
        buffer1.put(nameBytes);
        return buffer1;
    }


    private static User getUser(ByteBuffer buffer) {
        long id = buffer.getLong();
        int nameSize = buffer.getInt();
        final byte[] name = new byte[nameSize];
        buffer.get(name);
        return new User(id, new String(name, StandardCharsets.UTF_8));
    }


}
