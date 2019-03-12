package com.mzivkovic.domain;

public class Employee {
    private final long id;
    private final long departmentId;
    private final String name;

    public Employee(long id, long departmentId, String name) {
        this.id = id;
        this.departmentId = departmentId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    public String getName() {
        return name;
    }

}
