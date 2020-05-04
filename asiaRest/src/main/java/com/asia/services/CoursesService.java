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
    public List<Course> getAllCourses(@QueryParam("name") String name, @QueryParam("lecturer") String leturer) {
        return Main.getDatabase().getCourses(name, leturer);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewCourse(Course newCourse, @Context UriInfo uri) throws BadRequestException {
        if (newCourse.getName().length() > 0 && newCourse.getLecturer().length() > 0 ) {
            Course course = Main.getDatabase().addCourse(newCourse);
            UriBuilder builder = uri.getAbsolutePathBuilder();
            builder.path(Long.toString(course.getId()));
            return Response.created(builder.build()).entity(course).build();
        } else {
            return Response.noContent().status(400).build();
        }
    }
}
