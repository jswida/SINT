package com.bartek.models;

import com.bartek.rest.CourseService;
import com.bartek.rest.GradesService;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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
    List<Link> links;

    @XmlElement
    private Long id;
    @XmlElement
    private double value;
    @XmlElement
    private Date date;
    @XmlElement
    private Course course;
    @XmlTransient
    private long studentId;
//    @JsonIgnore
//    @Reference
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

    @Override
    public String toString() {
        return "Grade{" +
                "links=" + links +
                ", id=" + id +
                ", value=" + value +
                ", date=" + date +
                ", course=" + course +
                ", studentId=" + studentId +
                ", student=" + student +
                '}';
    }
}
