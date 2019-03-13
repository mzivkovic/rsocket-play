package com.mzivkovic.flow.domain;

public class Employee {
    private final User user;
    private final long departmentId;

    public Employee(User user, long departmentId) {
        this.user = user;
        this.departmentId = departmentId;
    }

    public User getUser() {
        return user;
    }

    public long getDepartmentId() {
        return departmentId;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "user=" + user +
                ", departmentId=" + departmentId +
                '}';
    }
}
