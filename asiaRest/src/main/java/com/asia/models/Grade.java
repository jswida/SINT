package com.asia.models;

import java.util.Date;
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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity(value = "grades")
//@Validation("{firstName: {$type: \"string\", $exists: true, $regex: \"^.+$\"}, " +
//        "lastName: {$type: \"string\", $exists: true, $regex: \"^.+$\"}, " +
//        "birthday: {$type: \"date\", $exists: true}, "+
//        "grades: {$not: {$elemMatch: { $or: [" +
//        "   {value: {$not: {$type: \"double\", $in: [2, 3, 3.5, 4, 4.5, 5], $exists: true}}}," +
//        "   {date: {$not: {$type: \"date\", $exists: true}}}," +
//        "   {couse: {$not: {$type: \"object\", $exists: true}}}" +
//        "] } } }" +
//        "}")
public class Grade {
    @InjectLinks({
            @InjectLink(resource = GradesService.class, rel = "parent"),
            @InjectLink(resource = CourseService.class, rel = "course",
                    bindings = {@Binding(name = "id", value = "${instance.course.id}")}),
            @InjectLink(value="students/{index}/grades/{id}", rel = "self",
                    bindings={
                            @Binding(name="index", value="${instance.studentIndex}"),
                            @Binding(name="id", value="${instance.id}")
                    }),
            @InjectLink(value="students/{index}", rel = "student",
                    bindings={@Binding(name="index", value="${instance.studentIndex}")}),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    @XmlTransient
    @Id
    ObjectId _id;
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    public ObjectId get_id() {
        return _id;
    }
    public void set_id(ObjectId id) {
        this._id = id;
    }

    @XmlElement
    @Indexed(options = @IndexOptions(unique = true))
    private Long id;
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
    private long studentIndex;

    public Grade( ) {
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

    @XmlTransient
    public long getStudentIndex() {
        return studentIndex;
    }

    public void setStudentIndex(long studentIndex) {
        this.studentIndex = studentIndex;
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
