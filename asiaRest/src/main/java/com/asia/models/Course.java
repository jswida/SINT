package com.asia.models;

import com.asia.services.CoursesService;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Course {
    @InjectLinks({
            @InjectLink(value="courses/{id}", rel = "self",
                    bindings={@Binding(name="id", value="${instance.id}")}),
            @InjectLink(resource = CoursesService.class, rel = "parent"),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    List<Link> links;

    @XmlElement
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

    public void setId(Long id) { this.id = id; }

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
        return "com.asia.models.Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lecturer='" + lecturer + '\'' +
                '}';
    }

    public void clearLinks() {
        this.links = null;
    }
}
