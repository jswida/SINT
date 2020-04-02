package com.bartek.rest;

import com.bartek.Storage;
import com.bartek.models.Grade;
import com.bartek.models.Student;
import com.bartek.models.Subject;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;

@Path("/grades")
public class GradeService {
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Set<Grade> getAllGrades() {
        return Storage.getGrades();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Grade getGradeById(@PathParam("id") long id) {
        for (Grade grade : Storage.getGrades()) {
            if (grade.getId() == id) {
                return grade;
            }
        }
        throw new NotFoundException();
    }

    @GET
    @Path("/{id}/subject")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Subject getSubjectOfGradeById(@PathParam("id") long id) {
        for (Grade grade : Storage.getGrades()) {
            if (grade.getId() == id) {
                Long subjectId = grade.getSubjectId();
                Subject subject = Storage.getSubjects().stream().filter(s -> s.getId().equals(subjectId)).findFirst().orElse(null);
                if (subject != null) return subject;
                else throw new NotFoundException();
            }
        }
        throw new NotFoundException();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void deleteGrade(@PathParam("id") long id) {
        Storage.delete(Grade.class, id);
    }


    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    @RolesAllowed("admin")
    public Response postNewGrade(Grade ng, @Context UriInfo uriInfo) throws BadRequestException {
        Grade grade = Storage.addGrade(ng);
//        student.clearLinks();

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(grade.getId()));
        return Response.created(builder.build()).entity(grade).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/student/{id}")
    public Response postNewGradeForStudent(Grade ng, @PathParam("id") long id, @Context UriInfo uriInfo) throws BadRequestException {
        Grade grade = Storage.addGradeToStudent(ng, id);
//        student.clearLinks();

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(grade.getId()));
        return Response.created(builder.build()).entity(grade).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    @RolesAllowed({"admin", "supervisor"})
    public Response updateGrade(@PathParam("id") Long updatedGradeId, Grade newGrade) throws NotFoundException {
        Grade grade = Storage.updateGrade(updatedGradeId, newGrade);
//        course.clearLinks();
        return Response.ok(grade).build();
    }
}
