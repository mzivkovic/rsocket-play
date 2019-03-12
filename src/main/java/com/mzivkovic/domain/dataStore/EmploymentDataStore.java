package com.mzivkovic.domain.dataStore;

import com.mzivkovic.domain.Employee;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

public class EmploymentDataStore {
    private final ConcurrentHashMap<Long, Employee> map = new ConcurrentHashMap<>();


    public void put(Employee employee) {
        map.put(employee.getId(), employee);
    }

    public Employee get(long id) {
        return map.get(id);
    }

    public Flux<Employee> departments() {
        return Flux.fromIterable(map.values());
    }
}
