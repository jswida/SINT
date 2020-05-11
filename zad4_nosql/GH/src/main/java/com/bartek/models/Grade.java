package com.bartek.models;

import com.bartek.nosql.ObjectIdJaxbAdapter;
import com.bartek.rest.CourseService;
import com.bartek.rest.GradesService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
        @Index(fields = @Field("id"), options = @IndexOptions(unique = true))
)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Grade {
    @InjectLinks({
            @InjectLink(
                    value="students/{index}/grades/{id}",
                    rel = "self",
                    bindings={
                            @Binding(name="index", value="${instance.studentId}"),
                            @Binding(name="id", value="${instance.id}")
                    }),
            @InjectLink(
                    value="students/{index}",
                    rel = "student",
                    bindings={
                            @Binding(name="index", value="${instance.studentId}")
                    }),
            @InjectLink(
                    resource = GradesService.class,
                    rel = "parent"
            ),
            @InjectLink(
                    resource = CourseService.class,
                    rel = "course",
                    bindings = {@Binding(name = "id", value = "${instance.course.id}")}
            ),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    @Transient
    List<Link> links;

    @XmlTransient
    @Id
    ObjectId _id;

    private Long id;

    private double value;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date date;

    @XmlElement(name = "course")
    @Reference
    private Course course;

    @XmlTransient
//    @JsonIgnore
//    @Reference
    private long studentId;

    @JsonIgnore
    @Reference
    @XmlTransient
    Student student;

    public Grade( ) {
    }


    public Grade(Long id, double value, Date date, Course course, long studentId, Student student) {
        this.id = id;
        this.value = value;
        this.date = date;
        this.course = course;
        this.studentId = studentId;
        this.student = student;
    }

    public Grade(Long id, double value, Date date, Course course) {
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


    public double getValue() {
        return value;
    }

    public void setValue(double value) {
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

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "links=" + links +
                ", _id=" + _id +
                ", id=" + id +
                ", value=" + value +
                ", date=" + date +
                ", course=" + course +
                ", studentId=" + studentId +
                ", student=" + student +
                '}';
    }
}
