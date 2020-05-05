package com.bartek.rest;

import com.bartek.Mango;
import com.bartek.Storage;
import com.bartek.models.Grade;
import com.bartek.models.Course;
import com.bartek.models.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Path("/students/{id}/grades")
public class GradesService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Grade> getStudentGrades(@PathParam("id") long id) {
        Student student = Mango.getMangoIns().getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) return student.getGrades();
        else throw new NotFoundException();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewGradeForStudent(Grade ng, @PathParam("id") long id, @Context UriInfo uriInfo) throws BadRequestException {
        List<Double> gradesList = Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0);
        if (gradesList.contains(ng.getValue()) && ng.getDate().toString().length() > 0 && ng.getCourse() != null){
            Student student = Mango.getMangoIns().getStudent(id);
            Grade grade = Mango.getMangoIns().addGrade(student, ng);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(grade.getId()));
            return Response.created(builder.build()).entity(grade).build();
        }
        else{
            return Response.noContent().status(400).build();
        }
    }




}
