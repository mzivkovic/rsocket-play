package com.mzivkovic.domain.serialization;

import com.mzivkovic.domain.Department;
import com.mzivkovic.domain.Employee;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.rsocket.Payload;
import io.rsocket.util.DefaultPayload;

import java.nio.charset.StandardCharsets;

public class Serialization {

    public static Payload toBuffer(Employee employee) {

        byte[] nameBytes = employee.getName().getBytes(StandardCharsets.UTF_8);

        int bufferSize = 8 + 8 + 4 + nameBytes.length;
        ByteBuf buffer = PooledByteBufAllocator.DEFAULT.buffer(bufferSize);
        buffer.writeLong(employee.getId());
        buffer.writeLong(employee.getDepartmentId());
        buffer.writeInt(nameBytes.length);
        buffer.writeBytes(nameBytes);
        return DefaultPayload.create(buffer);
    }

    public static Payload toBuffer(Department department) {

        byte[] nameBytes = department.getName().getBytes(StandardCharsets.UTF_8);
        int bufferSize = 8 + 4 + nameBytes.length;

        ByteBuf buffer = UnpooledByteBufAllocator.DEFAULT.compositeHeapBuffer(bufferSize);
        if ( buffer.refCnt()>1){
            System.out.println("HERE!!");
        }
        buffer.writeLong(department.getId());
        buffer.writeInt(nameBytes.length);
        buffer.writeBytes(nameBytes);

        return DefaultPayload.create(buffer);
    }

    public static Employee employeeFromBuffer(Payload payload) {

        ByteBuf buffer = payload.sliceData();

        long id = buffer.getLong(0);
        long departmentId = buffer.getLong(8);
        int nameSize = buffer.getInt(16);
        final byte[] name = new byte[nameSize];
        buffer.getBytes(20, name);

        buffer.release();

        return new Employee(id, departmentId, new String(name, StandardCharsets.UTF_8));
    }

    public static Department departmentFromBuffer(Payload payload) {

        ByteBuf buffer = payload.sliceData();

        long id = buffer.getLong(0);
        int nameSize = buffer.getInt(8);
        final byte[] name = new byte[nameSize];
        buffer.getBytes(12, name);

        return new Department(id, new String(name, StandardCharsets.UTF_8));
    }

}
