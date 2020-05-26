package com.bartek.rest;

import com.bartek.Main;
import com.bartek.models.Grade;
import com.bartek.models.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

@Path("/students/{id}/grades/{gradeId}")
public class GradeService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStudentGradeById(@PathParam("id") long id, @PathParam("gradeId") long gradeId) {
        Student student = Main.getDatabase().getStudentByID(id);
        if (student != null) {
            Grade grade = Main.getDatabase().getGradeByID(student, gradeId);
            if (grade != null) {
                return Response.ok(grade).build();
            } else {
                return Response.status(404).build();
            }
        }else {
            return Response.noContent().status(404).build();
        }
    }

    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteGrade(@PathParam("id") long id, @PathParam("gradeId") long gradeId) throws NotFoundException {
        Student student = Main.getDatabase().getStudentByID(id);
        if (student != null) {
            Grade grade = Main.getDatabase().getGradeByID(student, gradeId);
            if (grade != null) {
                Main.getDatabase().deleteGrade(grade);
                return Response.noContent().build();
            }
            else{
                return Response.noContent().status(404).build();
            }
        }
        else{
            return Response.noContent().status(404).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateStudentGradeById(@PathParam("id") long id, @PathParam("gradeId") long gradeId, Grade newGrade) {

        List<Double> gradesList = Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0);
        Student student = Main.getDatabase().getStudentByID(id);
        if (student != null) {
            Grade grade = Main.getDatabase().getGradeByID(student, gradeId);
            if (grade != null) {
                if (gradesList.contains(newGrade.getValue())) {
                    grade = Main.getDatabase().updateGrade(student, grade, newGrade);
                    return Response.ok(grade).status(204).build();
                } else {
                    return Response.noContent().status(400).build();
                }
            } else {
                return Response.noContent().status(400).build();
            }
        } else {
            return Response.noContent().status(400).build();
        }
    }



}
