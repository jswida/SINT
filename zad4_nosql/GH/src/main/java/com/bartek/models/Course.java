package com.bartek.models;

import com.bartek.rest.CourseService;
import com.bartek.rest.CoursesService;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@Entity(value = "courses")
public class Course {
    @InjectLinks({
            @InjectLink(
                    value="courses/{id}",
                    rel = "self",
                    bindings={
                    @Binding(name="id", value="${instance.id}")
            }),
            @InjectLink(
                    resource = CoursesService.class,
                    rel = "parent"
            ),
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
    private Long id;
    @XmlElement
    private String name;
    @XmlElement
    private String lecturer;

    public Course() {
    }

    public Course(Long id, String name, String lecturer) {
        this.id = id;
        this.name = name;
        this.lecturer = lecturer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    @Override
    public String toString() {
        return "com.bartek.models.Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lecturer='" + lecturer + '\'' +
                '}';
    }
}
