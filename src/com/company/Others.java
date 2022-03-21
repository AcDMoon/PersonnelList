package com.company;

public class Others extends Personnel{
    private String textDescription;

    public Others(String employeePosition, String fullName, String DOB, String turn_to, String textDescription) {
        super(employeePosition, fullName, DOB, turn_to);
        this.textDescription = textDescription;
    }

    public String get_textDescription() {
        return textDescription;
    }
}
