package com.bartek.rest;

import com.bartek.Main;
import com.bartek.models.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/courses/{id}")
public class CourseService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Course getCourseById(@PathParam("id") long id) {
        return Main.getDatabase().getCourseByID(id);

    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteCourse(@PathParam("id") long id) throws NotFoundException {
        Course course = Main.getDatabase().getCourseByID(id);
        if (course != null) {
            Main.getDatabase().deleteCourse(course);
            return Response.noContent().build();
        } else {
            return Response.noContent().status(404).build();
        }
    }


    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateCourse(@PathParam("id") Long updatedCourseId, Course newCourse) throws NotFoundException {
        Course course = Main.getDatabase().getCourseByID(updatedCourseId);
        if (newCourse.getName().length() > 0 && newCourse.getLecturer().length() > 0) {
            course = Main.getDatabase().updateCourse(course, newCourse);
            return Response.ok(course).status(204).build();
        } else {
            return Response.ok(course).status(400).build();
        }
    }
}
