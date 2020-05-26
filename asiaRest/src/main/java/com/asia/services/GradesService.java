package com.asia.services;

import com.asia.Main;
import com.asia.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Path("/students/{id}/grades")
public class GradesService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Grade> getStudentGrades(@PathParam("id") long id,
                                        @QueryParam("course") String courseId,
                                        @QueryParam("value") double value,
                                        @QueryParam("valueCompare") String compare,
                                        @QueryParam("date") String date,
                                        @QueryParam("dateCompare") String dateCompare
                                        ) {
        Date gradeDate = new DateParamConverterProvider("yyyy-MM-dd").getConverter(Date.class, Date.class, null).fromString(date);
        Student student = Main.getDatabase().getStudentByID(id);
        if (student != null){
            return Main.getDatabase().getGradesFiltered(id, courseId, value, compare, gradeDate, dateCompare);

        }
        else throw new NotFoundException();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewGradeForStudent(Grade ng, @PathParam("id") long id, @Context UriInfo uriInfo) throws BadRequestException {
        List<Double> gradesList = Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0);
        Student student = Main.getDatabase().getStudentByID(id);
        if (gradesList.contains(ng.getValue()) && ng.getDate().toString().length() > 0 && ng.getCourse() != null){
            Grade grade = Main.getDatabase().addGrade(student, ng.getCourse(), ng);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(grade.getId()));
            return Response.created(builder.build()).entity(grade).build();
        }
        else{
            return Response.noContent().status(400).build();
        }
    }
}
