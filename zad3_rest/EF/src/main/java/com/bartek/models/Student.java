package com.bartek.models;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
public class Student {
    private Long index; // ID
    private String name;
    private String surname;
    private Date birthDate;
    private Set<Grade> grades = new HashSet<>();

    public Student() {
    }

    public Student(Long id, String name, String surname, Date birthDate, Set<Grade> grades) {
        this.index = id;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.grades = grades;
    }

    public Long getId() {
        return index;
    }

    public void setId(Long id) {
        this.index = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    public void setGrade(Grade grade) {
        this.grades.add(grade);
    }

    @Override
    public String toString() {
        return "com.bartek.models.Student{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", birthDate=" + birthDate +
                ", grades=" + grades +
                '}';
    }
}
