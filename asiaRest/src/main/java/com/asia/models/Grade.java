package com.asia.models;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Grade {
    @XmlElement
    private Long id;
    @XmlElement
    private GradeValue value;
    @XmlElement
    private Date date;
    @XmlElement
    private Course course;

    public Grade( ) {
        System.out.println("pusty");
    }


    public Grade(Long id, GradeValue value, Date date, Course course) {
        System.out.println("pełny");
        this.id = id;
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

    public GradeValue getValue() {
        return value;
    }

    public void setValue(GradeValue value) {
        this.value = value;
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
