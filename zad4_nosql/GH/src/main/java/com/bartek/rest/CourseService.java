package com.bartek.rest;


import com.bartek.Storage;
import com.bartek.models.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/courses/{id}")
public class CourseService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Course getCourseById(@PathParam("id") long id) {
        for (Course course : Storage.getCourses()) {
            if (course.getId() == id) {
                return course;
            }
        }
        throw new NotFoundException();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteCourse(@PathParam("id") long id) throws NotFoundException {
        Course course = Storage.getCourses().stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
        if (course != null) {
            System.out.println("Delete Course: " + course.toString());
            Storage.delete(Course.class, id);
            return Response.noContent().build();
        } else {
            return Response.noContent().status(404).build();
        }
    }


    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateCourse(@PathParam("id") Long updatedCourseId, Course newCourse) throws NotFoundException {
        Course course = Storage.getCourses().stream().filter(s -> s.getId().equals(updatedCourseId)).findFirst().orElse(null);
        if (newCourse.getName() != null && newCourse.getLecturer() != null) {
            course = Storage.updateCourse(updatedCourseId, newCourse);
            return Response.ok(course).status(204).build();
        } else {
            return Response.ok(course).status(400).build();
        }
    }
}
