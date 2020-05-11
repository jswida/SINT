package com.bartek.models;

import com.bartek.rest.CoursesService;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.*;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@Entity
@XmlRootElement
@Indexes(
        @Index(fields = {@Field("id")}, options = @IndexOptions(unique = true))
)
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
    @Transient
    List<Link> links;

    @XmlTransient
    @Id
    ObjectId _id;

    private Long id;

    private String name;

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

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "Course{" +
                "links=" + links +
                ", _id=" + _id +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", lecturer='" + lecturer + '\'' +
                '}';
    }
}
