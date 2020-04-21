package com.asia.services;

import com.asia.models.Course;
import com.asia.DataBase;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/courses")
public class CourseService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Course> getAllCourses(){
        return DataBase.getCourses();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Course getCourseById(@PathParam("id") long id){
        for (Course course : DataBase.getCourses()){
            if (course.getId() == id){
                return course;
            }
        }
        throw new NotFoundException();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void deleteCourse(@PathParam("id") long id){
        DataBase.delete(Course.class, id);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewCourse(Course newCourse, @Context UriInfo uriInfo) throws BadRequestException {
        if (newCourse.getName() != null && newCourse.getLecturer() != null && newCourse.getLecturer().toString().length() > 0 && newCourse.getName().toString().length() > 0) {
            Course course = DataBase.addCourse(newCourse);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(course.getId()));
            return Response.created(builder.build()).entity(course).build();
        }
        else {
            return Response.ok(newCourse).status(400).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateCourse(@PathParam("id") Long updatedCourseId, Course newCourse) throws NotFoundException {
        Course course = DataBase.getCourses().stream().filter(s -> s.getId().equals(updatedCourseId)).findFirst().orElse(null);
        if (newCourse.getName() != null && newCourse.getLecturer() != null && newCourse.getLecturer().toString().length() > 0 && newCourse.getName().toString().length() > 0) {
            course = DataBase.updateCourse(updatedCourseId, newCourse);
            return Response.ok(course).status(204).build();
        }
        else{
            return Response.ok(course).status(400).build();
        }
    }

}
