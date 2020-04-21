package com.asia.models;

public enum GradeValue {
    NIEDOSTATECZNY(2.0),
    DOSTATECZNY(3.0),
    DOSTATECZNY_PLUS(3.5),
    DOBRY(4.0),
    DOBRY_PLUS(4.5),
    BARDZO_DOBRY(5.0);

    GradeValue(double v) { }

    private double gradevalue;

    public double getValue() {
        return gradevalue;
    }

    public void setValue(double v) {
        this.gradevalue = v;
    }

};