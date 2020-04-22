package com.bartek.models;

import com.bartek.rest.CourseService;
import com.bartek.rest.GradeService;
import com.bartek.rest.StudentService;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Student {
    @InjectLinks({
            @InjectLink(
                    resource =  StudentService.class,
                    rel = "self"),
            @InjectLink(resource = StudentService.class, rel = "parent"),
            @InjectLink(resource = GradeService.class, rel = "grades"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    @XmlElement
    private Long index; // index
    @XmlElement
    private String firstName;
    @XmlElement
    private String lastName;
    @XmlElement
    private Date birthday;
    @XmlTransient
    private Set<Grade> grades = new HashSet<>();

    public Student() {
    }

    public Student(Long index, String firstName, String lastName, Date birthday, Set<Grade> grades) {
        this.index = index;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.grades = grades;
    }


    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setSurname(String lastName) {
        this.lastName = lastName;
    }

    public Date getBrithday() {
        return birthday;
    }

    public void setBirthDate(Date birthday) {
        this.birthday = birthday;
    }

    @XmlTransient
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
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                ", grades=" + grades +
                '}';
    }

    public void clearLinks() {
        this.links = null;
    }
}
