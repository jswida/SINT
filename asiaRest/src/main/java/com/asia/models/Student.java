package com.asia.models;

import com.asia.services.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import dev.morphia.annotations.*;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity(value = "students")
@Validation("{firstName: {$type: \"string\", $exists: true, $regex: \"^.+$\"}, " +
            "lastName: {$type: \"string\", $exists: true, $regex: \"^.+$\"}, " +
            "birthday: {$type: \"date\", $exists: true}, "+
            "grades: {$not: {$elemMatch: { $or: [" +
            "   {value: {$not: {$type: \"double\", $in: [2, 3, 3.5, 4, 4.5, 5], $exists: true}}}," +
            "   {date: {$not: {$type: \"date\", $exists: true}}}," +
            "   {couse: {$not: {$type: \"object\", $exists: true}}}" +
            "] } } }" +
            "}")
public class Student {
    @InjectLinks({
            @InjectLink(resource = StudentsService.class, rel = "parent"),
            @InjectLink(value="students/{index}", rel="self",
                    bindings={@Binding(name="index", value="${instance.index}")}),
            @InjectLink(value="students/{index}/grades", rel = "grades",
                    bindings={@Binding(name="index", value="${instance.index}")}),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    @Id
    @XmlTransient
    private ObjectId _id;
    @XmlElement
    @Indexed(options = @IndexOptions(unique = true))
    private Long index;
    @XmlElement
    private String firstName;
    @XmlElement
    private String lastName;
    @XmlElement
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date birthday;
    @XmlTransient
    @Embedded
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @XmlTransient
    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
        for (Grade grade : this.grades){
            grade.setStudentIndex(this.index);
        }
    }

    public void setGrade(Grade grade) {
        this.grades.add(grade);
    }


    @Override
    public String toString() {
        return "com.asia.models.Student{" +
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
