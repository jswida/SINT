package com.bartek.rest;

import com.bartek.Storage;
import com.bartek.models.Grade;
import com.bartek.models.Student;
import com.bartek.models.Subject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

@Path("/grades")
public class GradeService {
    @GET
    public Set<Grade> getAllGrades(){
        return Storage.getGrades();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Grade getGradeById(@PathParam("id") long id){
        for (Grade grade : Storage.getGrades()){
            if (grade.getId() == id){
                return grade;
            }
        }
        throw new NotFoundException();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void deleteGrade(@PathParam("id") long id){
        Storage.delete(Grade.class, id);
    }
}
