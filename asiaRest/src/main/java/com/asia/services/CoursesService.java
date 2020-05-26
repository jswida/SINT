package com.asia.services;

import com.asia.DataBase;
import com.asia.Main;
import com.asia.models.Course;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Set;

@Path("/courses")
public class CoursesService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Course> getAllCourses(@QueryParam("name") String name, @QueryParam("lecturer") String lecturer) {
        return Main.getDatabase().getCoursesFiltered(name, lecturer);
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
