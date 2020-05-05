package com.bartek.rest;


import com.bartek.Mango;
import com.bartek.models.Course;
import com.bartek.models.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Set;

@Path("/courses")
public class CoursesService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Course> getAllCourses() {
        return Mango.getMangoIns().getCourses();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewCourse(Course ns, @Context UriInfo uriInfo) throws BadRequestException {
        if (ns.getName().length() > 0 && ns.getLecturer().length() > 0 ) {
            Course course = Mango.getMangoIns().addCourse(ns);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(course.getId()));
            return Response.created(builder.build()).entity(course).build();
        } else {
            return Response.noContent().status(400).build();
        }
    }

}
