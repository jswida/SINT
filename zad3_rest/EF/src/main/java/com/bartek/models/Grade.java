package com.bartek.models;

import javax.xml.bind.annotation.*;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Grade {
    @XmlElement
    private Long id;
//    @XmlElement
//    private GradeValue valueGV;
    @XmlElement
    private double value;
    @XmlElement
    private Date date;
    @XmlElement
    private Course course;

    public Grade( ) {
    }


//    public Grade(Long id, GradeValue valueGV, Date date, Course course) {
//        this.id = id;
//        this.valueGV = valueGV;
//        this.date = date;
//        this.course = course;
//    }

    public Grade(Long id, String value, Date date, Course course) {
        this.id = id;
//        this.valueGV = GradeValue.valueOf(value);
        this.value = Double.parseDouble(value);
        this.date = date;
        this.course = course;
    }

    public Grade(Long id, double value, Date date, Course course) {
        this.id = id;
//        this.valueGV = GradeValue.valueOf(String.valueOf(value));
        this.value = value;
        this.date = date;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public GradeValue getValueGV() {
//        return valueGV;
//    }

//    @XmlElement(name="value")
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
//        this.valueGV = GradeValue.valueOf(String.valueOf(value));
        this.value = value;
    }

    public void setValueGV(GradeValue valueGV) {
//        this.valueGV = valueGV;
        this.value = valueGV.getValue();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", value=" + value +
                ", date=" + date +
                ", course=" + course +
                '}';
    }
}
