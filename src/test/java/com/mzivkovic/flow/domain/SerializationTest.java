package com.mzivkovic.flow.domain;

import io.rsocket.Payload;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SerializationTest {

    @Test
    public void serializationEmployee() {
        User user = new User(32_786, "Some very long name");
        Employee employee = new Employee(user, 90_230);
        Payload payload = Serialization.to(employee);

        Employee fromBuffer = Serialization.getEmployee(payload);

        assertThat(fromBuffer.getUser().getId()).isEqualTo(user.getId());
        assertThat(fromBuffer.getUser().getName()).isEqualTo(user.getName());
        assertThat(fromBuffer.getDepartmentId()).isEqualTo(employee.getDepartmentId());
    }

    @Test
    public void serializationDepartment() {
        User user = new User(32_786, "Some very long name");
        Payload buf = Serialization.to(user);

        User fromBuffer = Serialization.getUser(buf);

        assertThat(fromBuffer.getId()).isEqualTo(user.getId());
        assertThat(fromBuffer.getName()).isEqualTo(user.getName());
    }
}