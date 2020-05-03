package com.asia.services;

import com.asia.models.Course;
import com.asia.DataBase;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/courses/{id}")
public class CourseService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Course getCourseById(@PathParam("id") long id) {
        for (Course course : DataBase.getCourses()) {
            if (course.getId() == id) {
                return course;
            }
        }
        throw new NotFoundException();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteCourse(@PathParam("id") long id) throws NotFoundException {
        Course course = DataBase.getCourses().stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
        if (course != null) {
            DataBase.delete(Course.class, id);
            return Response.noContent().build();
        } else {
            return Response.noContent().status(404).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateCourse(@PathParam("id") Long updatedCourse, Course newCourse) throws NotFoundException {
        Course course = DataBase.getCourses().stream().filter(s -> s.getId().equals(updatedCourse)).findFirst().orElse(null);
        if (newCourse.getName() != null && newCourse.getName().length() > 0 && newCourse.getLecturer() != null && newCourse.getLecturer().length() > 0 && course.getId() != null) {
            course = DataBase.updateCourse(updatedCourse, newCourse);
            return Response.ok(course).status(204).build();
        } else {
            return Response.ok(course).status(400).build();
        }
    }
}
