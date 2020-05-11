package com.bartek.rest;

import com.bartek.Storage;
import com.bartek.models.Grade;
import com.bartek.models.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/students/{id}/grades/{gradeId}")
public class GradeService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStudentGradeById(@PathParam("id") long id, @PathParam("gradeId") long gradeId) {
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) {
            Grade grade = Storage.getGrades().stream().filter(s -> s.getStudentId() == id && s.getId() == gradeId).findFirst().orElse(null);
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
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex().equals(id)).findFirst().orElse(null);
        if (student != null) {
            Grade grade = Storage.getGrades().stream().filter(s -> s.getStudentId() == id && s.getId() == gradeId).findFirst().orElse(null);
            if (grade != null) {
                Storage.delete(Grade.class, gradeId);
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
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex().equals(id)).findFirst().orElse(null);
        if (student != null) {
            Grade grade = Storage.getGrades().stream().filter(s -> s.getStudentId() == id && s.getId() == gradeId).findFirst().orElse(null);
            if (grade != null) {
                if (gradesList.contains(newGrade.getValue())) {
                    grade = Storage.updateGrade(gradeId, newGrade);
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
