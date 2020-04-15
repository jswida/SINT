package com.bartek.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement
//@XmlAccessorType(XmlAccessType.FIELD)
public class Grade {

    private Long id;
    private GradeValue value;
    private Date date;
    private Long subjectId;

    public Grade( ) {
    }


    public Grade(Long id, GradeValue value, Date date, Long subjectId) {
        this.id = id;
        this.value = value;
        this.date = date;
        this.subjectId = subjectId;
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


    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", value=" + value +
                ", date=" + date +
                ", subjectId=" + subjectId +
                '}';
    }
}
