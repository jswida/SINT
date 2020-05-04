package com.asia.services;

import com.asia.Main;
import com.asia.models.Course;
import com.asia.DataBase;
import com.asia.models.Model;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/courses/{id}")
public class CourseService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getCourseById(@PathParam("id") long id) {
        Course course = Main.getDatabase().getCourse(id);
        if (course != null){
            return Response.ok(course).build();
        }
        throw new NotFoundException();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteCourse(@PathParam("id") long id) throws NotFoundException {
        Course course = Model.getInstance().getCourse(id);
        if (course != null) {
            Model.getInstance().deleteCourse(course);
            return Response.noContent().build();
        } else {
            return Response.noContent().status(404).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateCourse(@PathParam("id") Long updatedCourse, Course newCourse) throws NotFoundException {
        Course course = Main.getDatabase().getCourse(updatedCourse);
        if (newCourse.getName() != null && newCourse.getName().length() > 0 && newCourse.getLecturer() != null && newCourse.getLecturer().length() > 0 && course.getId() != null) {
            course = Main.getDatabase().updateCourse(course, newCourse);
            return Response.ok(course).status(204).build();
        } else {
            return Response.ok(course).status(400).build();
        }
    }
}
