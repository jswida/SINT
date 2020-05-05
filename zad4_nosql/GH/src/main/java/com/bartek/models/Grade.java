package com.bartek.models;

import com.bartek.rest.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import dev.morphia.annotations.*;
import org.bson.types.ObjectId;
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
@Entity(value = "grades")
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
    @Indexed(options = @IndexOptions(unique = true))
    private Long id;
//    @XmlElement
//    private GradeValue valueGV;
    @XmlElement
    private double value;
    @XmlElement
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "CET")
    private Date date;
    @XmlElement
    @Reference
    private Course course;
    @XmlTransient
    @Reference
    private long studentId; //student index

    @Id
    @XmlTransient
    private ObjectId _id = new ObjectId();
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    public ObjectId get_id() {
        return _id;
    }
    public void set_id(ObjectId id) {
        this._id = id;
    }

    public Grade( ) {
    }



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

    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
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
                ", _id=" + _id +
                '}';
    }
}
