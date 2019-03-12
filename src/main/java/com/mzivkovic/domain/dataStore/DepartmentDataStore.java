package com.mzivkovic.domain.dataStore;

import com.mzivkovic.domain.Department;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

public class DepartmentDataStore {

    private final ConcurrentHashMap<Long, Department> map = new ConcurrentHashMap<>();

    public void init(long number) {
        for (long i = 0; i < number; i++) {
            put(new Department(i, "department_" + i));
        }
    }


    public void put(Department department) {
        map.put(department.getId(), department);
    }

    public Department get(long id) {
        return map.get(id);
    }

    public Flux<Department> departments() {
        return Flux.fromIterable(map.values());
    }

}
