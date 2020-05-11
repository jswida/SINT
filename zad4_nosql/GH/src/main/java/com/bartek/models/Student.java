package com.bartek.models;

import com.bartek.rest.StudentsService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.*;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@Entity
@XmlRootElement
@Indexes(
        @Index(fields = {@Field("index")}, options = @IndexOptions(unique = true))
)
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
    @Transient
    List<Link> links;

    @XmlTransient
    @Id
    ObjectId _id;


    private Long index; // index

    private String firstName;

    private String lastName;

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="CET")
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

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Student{" +
                "links=" + links +
                ", _id=" + _id +
                ", index=" + index +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
