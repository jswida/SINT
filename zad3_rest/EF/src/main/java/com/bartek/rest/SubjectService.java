package com.bartek.rest;


import com.bartek.Storage;
import com.bartek.models.Grade;
import com.bartek.models.Student;
import com.bartek.models.Subject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Path("/subjects")
public class SubjectService {

    @GET
    public Set<Subject> getAllSubjects(){
        return Storage.getSubjects();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Subject getSubjectById(@PathParam("id") long id){
        for (Subject subject : Storage.getSubjects()){
            if (subject.getId() == id){
                return subject;
            }
        }
        throw new NotFoundException();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void deleteSubject(@PathParam("id") long id){
        Storage.delete(Subject.class, id);
    }

}
