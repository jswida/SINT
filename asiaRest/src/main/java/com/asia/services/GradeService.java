package com.asia.services;


import com.asia.DataBase;
import com.asia.models.Grade;
import com.asia.models.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/grades")
public class GradeService {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Grade> getAllGrades() {
        return DataBase.getGrades();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Grade getGradeById(@PathParam("id") long id) {
        for (Grade grade : DataBase.getGrades()) {
            if (grade.getId() == id) {
                return grade;
            }
        }
        throw new NotFoundException();
    }

    @GET
    @Path("/{id}/Course")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Course getCourseOfGradeById(@PathParam("id") long id) {
        for (Grade grade : DataBase.getGrades()) {
            if (grade.getId() == id) {
                Long CourseId = grade.getCourse().getId();
                Course course = DataBase.getCourses().stream().filter(s -> s.getId().equals(CourseId)).findFirst().orElse(null);
                if (course != null) return course;
                else throw new NotFoundException();
            }
        }
        throw new NotFoundException();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void deleteGrade(@PathParam("id") long id) {
        DataBase.delete(Grade.class, id);
    }


    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewGrade(Grade ng, @Context UriInfo uriInfo) throws BadRequestException {
        Grade grade = DataBase.addGrade(ng);
//        student.clearLinks();

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(grade.getId()));
        return Response.created(builder.build()).entity(grade).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/student/{id}")
    public Response postNewGradeForStudent(Grade ng, @PathParam("id") long id, @Context UriInfo uriInfo) throws BadRequestException {
        Grade grade = DataBase.addGradeToStudent(ng, id);
//        student.clearLinks();

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(grade.getId()));
        return Response.created(builder.build()).entity(grade).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateGrade(@PathParam("id") Long updatedGradeId, Grade newGrade) throws NotFoundException {
        Grade grade = DataBase.getGrades().stream().filter(s -> s.getId().equals(updatedGradeId)).findFirst().orElse(null);
        System.out.println("dod");
        if (newGrade.getDate() != null && newGrade.getValue() != null) {
            grade = DataBase.updateGrade(updatedGradeId, newGrade);
            return Response.ok(grade).status(204).build();
        }
        return Response.ok(grade).status(400).build();
    }
}