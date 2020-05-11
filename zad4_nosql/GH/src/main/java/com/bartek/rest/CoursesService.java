package com.bartek.rest;


import com.bartek.Main;
import com.bartek.models.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/courses")
public class CoursesService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Course> getAllCourses() {
        return Main.getDatabase().getCourses();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewCourse(Course ns, @Context UriInfo uriInfo) throws BadRequestException {
        if (ns.getName().length() > 0 && ns.getLecturer().length() > 0 ) {
            Course course = Main.getDatabase().addCourse(ns);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(course.getId()));
            return Response.created(builder.build()).entity(course).build();
        } else {
            return Response.noContent().status(400).build();
        }
    }


}
