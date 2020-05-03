package com.asia.services;


import com.asia.DataBase;
import com.asia.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Path("/students/{index}/grades/{gradeId}")
public class GradeService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStudentGradeById(@PathParam("index") long index, @PathParam("gradeId") long gradeId) {
        Student student = DataBase.getStudents().stream().filter(s -> s.getIndex() == index).findFirst().orElse(null);
        if (student != null) {
            Grade grade = student.getGrades().stream().filter(s -> s.getId().equals(gradeId)).findFirst().orElse(null);
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
    public Response deleteStudent(@PathParam("index") long index, @PathParam("gradeId") long gradeId) throws NotFoundException {
        Student student = DataBase.getStudents().stream().filter(s -> s.getIndex().equals(index)).findFirst().orElse(null);
        if (student != null) {
            Grade grade = student.getGrades().stream().filter(s -> s.getId().equals(gradeId)).findFirst().orElse(null);
            if (grade != null) {
                DataBase.delete(Grade.class, gradeId);
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
    public Response updateStudentGradeById(@PathParam("index") long index, @PathParam("gradeId") long gradeId, Grade newGrade) {

        List<Double> gradesValues = Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0);
        Student student = DataBase.getStudents().stream().filter(s -> s.getIndex().equals(index)).findFirst().orElse(null);
        if (student != null) {
            Grade grade = student.getGrades().stream().filter(s -> s.getId().equals(gradeId)).findFirst().orElse(null);
            if (grade != null) {
                if (gradesValues.contains(newGrade.getValue())) {
                    grade = DataBase.updateGrade(gradeId, newGrade);
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