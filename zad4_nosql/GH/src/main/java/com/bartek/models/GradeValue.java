package com.bartek.models;

public enum GradeValue {

    TWO(2.0),
    THREE(3.0),
    THREEANDHALF(3.5),
    FOUR(4.0),
    FOURANDHALF(4.5),
    FIVE(5.0);


    private double value;

    private GradeValue(double v) {
        this.value = v;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double v){
        this.value = v;
    }
};
