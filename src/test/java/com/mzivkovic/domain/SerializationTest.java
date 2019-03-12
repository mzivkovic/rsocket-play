package com.mzivkovic.domain;

import com.mzivkovic.domain.serialization.Serialization;
import io.netty.buffer.ByteBuf;
import io.rsocket.Payload;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {

    @Test
    public void serializationEmployee() {
        Employee employee = new Employee(987, 8_987, "Some very long name");
        Payload buf = Serialization.toBuffer(employee);

        Employee fromBuffer = Serialization.employeeFromBuffer(buf);

        assertThat(fromBuffer.getId()).isEqualTo(employee.getId());
        assertThat(fromBuffer.getDepartmentId()).isEqualTo(employee.getDepartmentId());
        assertThat(fromBuffer.getName()).isEqualTo(employee.getName());
    }

    @Test
    public void serializationDepartment() {
        Department department = new Department(32_786, "Some very long name");
        Payload buf = Serialization.toBuffer(department);

        Department fromBuffer = Serialization.departmentFromBuffer(buf);

        assertThat(fromBuffer.getId()).isEqualTo(department.getId());
        assertThat(fromBuffer.getName()).isEqualTo(department.getName());
    }
}