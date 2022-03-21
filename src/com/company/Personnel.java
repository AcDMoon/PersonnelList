package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Personnel {
    private String employeePosition;
    private String fullName;
    private String DOB;
    private String turn_to;

    public Personnel(String employeePosition, String fullName, String DOB, String turn_to) {
        this.employeePosition = employeePosition;
        this.fullName = fullName;
        this.DOB = DOB;
        this.turn_to = turn_to;
    }
    public String get_employeePosition() {
        return employeePosition;
    }
    public String get_fullName() {
        return fullName;
    }
    public String get_DOB() {
        return DOB;
    }
    public String get_turn_to() {
        return turn_to;
    }
    public Integer get_year(){
        List<String> strData = Arrays.asList(turn_to.split("\\."));
        List<Integer> intData = new ArrayList<>();
        for (String l : strData){
            intData.add(Integer.valueOf(l));
        }
    return intData.get(2);
    }

    public Integer get_month(){
        List<String> strData = Arrays.asList(turn_to.split("\\."));
        List<Integer> intData = new ArrayList<>();
        for (String l : strData){
            intData.add(Integer.valueOf(l));
        }
        return intData.get(1);
    }

    public Integer get_day(){
        List<String> strData = Arrays.asList(turn_to.split("\\."));
        List<Integer> intData = new ArrayList<>();
        for (String l : strData){
            intData.add(Integer.valueOf(l));
        }
        return intData.get(0);
    }

}
