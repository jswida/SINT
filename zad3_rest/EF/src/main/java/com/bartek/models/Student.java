package com.bartek.models;

import com.bartek.rest.GradeService;
import com.bartek.rest.GradesService;
import com.bartek.rest.StudentService;
import com.bartek.rest.StudentsService;
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
                    value="students/{index}",
                    rel="self",
                    bindings={
                        @Binding(name="index", value="${instance.index}")
            }),
            @InjectLink(
                    resource = StudentsService.class,
                    rel = "parent"
            ),
            @InjectLink(
                    value="students/{index}/grades",
                    rel = "grades",
                    bindings={
                    @Binding(name="index", value="${instance.index}")
            }),
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

    public Student() {
    }

    public Student(Long index, String firstName, String lastName, Date birthday) {
        this.index = index;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
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

    @Override
    public String toString() {
        return "com.bartek.models.Student{" +
                "index=" + index +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                '}';
    }

    public void clearLinks() {
        this.links = null;
    }
}
