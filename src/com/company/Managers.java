package com.company;

public class Managers extends Personnel {
    private String workerList;

    public Managers(String employeePosition, String fullName, String DOB, String turn_to, String workerList) {
        super(employeePosition, fullName, DOB, turn_to);
        this.workerList = workerList;
    }

    public String get_workerList() {
        return workerList;
    }

    public void set_workerList(String workerList) {
        this.workerList = workerList;
    }
}

