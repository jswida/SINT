package com.asia.services;

import com.asia.DataBase;
import com.asia.Main;
import com.asia.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.awt.event.MouseAdapter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Path("students/{index}/grades")
public class GradesService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Grade> getStudentGrades(@PathParam("index") long index, @QueryParam("course_id") Long courseId, @QueryParam("grade") double val, @QueryParam("order") String order) {
        Student student = Main.getDatabase().getStudent(index);
        Course course = Main.getDatabase().getCourse(courseId);
        if (student != null) {
            return Main.getDatabase().getGrades(index, course, val, order);
        }
        else throw new NotFoundException();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewGradeForStudent(Grade newGrade, @PathParam("index") long index, @Context UriInfo uriInfo) throws BadRequestException {
        List<Double> gradesValues = Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0);
        if (gradesValues.contains(newGrade.getValue()) && newGrade.getDate().toString().length() > 0 && newGrade.getCourse() != null){
            Student student = Main.getDatabase().getStudent(index);
            Course course = Main.getDatabase().getCourse(newGrade.getCourse().getId());
            Grade grade = Main.getDatabase().addGrade(student, course, newGrade);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(grade.getId()));
            return Response.created(builder.build()).entity(grade).build();
        }
        else{
            return Response.noContent().status(400).build();
        }
    }

}
