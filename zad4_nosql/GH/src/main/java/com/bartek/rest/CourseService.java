package com.bartek.rest;


import com.bartek.Mango;
import com.bartek.models.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/courses/{id}")
public class CourseService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Course getCourseById(@PathParam("id") long id) {
        for (Course course : Mango.getMangoIns().getCourses()) {
            if (course.getId() == id) {
                return course;
            }
        }
        throw new NotFoundException();
    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteCourse(@PathParam("id") long id) throws NotFoundException {
        Course course = Mango.getMangoIns().getCourse(id);
        if (course != null) {
            System.out.println("Delete Course: " + course.toString());
            Mango.getMangoIns().deleteCourse(course);
            return Response.noContent().build();
        } else {
            return Response.noContent().status(404).build();
        }
    }


    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateCourse(@PathParam("id") Long updatedCourseId, Course newCourse) throws NotFoundException {
        Course course = Mango.getMangoIns().getCourse(updatedCourseId);
        if (newCourse.getName() != null && newCourse.getLecturer() != null) {
            course = Mango.getMangoIns().updateCourse(course, newCourse);
            return Response.ok(course).status(204).build();
        } else {
            return Response.ok(course).status(400).build();
        }
    }
}
