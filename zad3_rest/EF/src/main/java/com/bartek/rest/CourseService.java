package com.bartek.rest;


import com.bartek.Storage;
import com.bartek.models.Course;
import com.bartek.models.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Set;

@Path("/courses")
public class CourseService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Course> getAllCourses() {
        return Storage.getCourses();
    }

//    @GET
//    @Path("/{id}")
//    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//    public Course getCourseById(@PathParam("id") long id) {
//        for (Course course : Storage.getCourses()) {
//            if (course.getId() == id) {
//                return course;
//            }
//        }
//        throw new NotFoundException();
//    }

//    @DELETE
//    @Path("/{id}")
//    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//    public Response deleteCourse(@PathParam("id") long id) throws NotFoundException {
//        Course course = Storage.getCourses().stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
//        if (course != null) {
//            System.out.println("Delete Course: " + course.toString());
//            Storage.delete(Course.class, id);
//            return Response.noContent().build();
//        } else {
//            return Response.noContent().status(404).build();
//        }
//    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewCourse(Course ns, @Context UriInfo uriInfo) throws BadRequestException {
        if (ns.getName().length() > 0 && ns.getLecturer().length() > 0 ) {
            Course course = Storage.addCourse(ns);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(course.getId()));
            return Response.created(builder.build()).entity(course).build();
        } else {
            return Response.noContent().status(400).build();
        }
    }

//    @PUT
//    @Path("/{id}")
//    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//    public Response updateCourse(@PathParam("id") Long updatedCourseId, Course newCourse) throws NotFoundException {
//        Course course = Storage.getCourses().stream().filter(s -> s.getId().equals(updatedCourseId)).findFirst().orElse(null);
//        if (newCourse.getName() != null && newCourse.getLecturer() != null) {
//            course = Storage.updateCourse(updatedCourseId, newCourse);
//            return Response.ok(course).status(204).build();
//        } else {
//            return Response.ok(course).status(400).build();
//        }
//    }
}
