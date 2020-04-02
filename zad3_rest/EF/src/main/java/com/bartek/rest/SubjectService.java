package com.bartek.rest;


import com.bartek.Storage;
import com.bartek.models.Grade;
import com.bartek.models.Student;
import com.bartek.models.Subject;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Path("/subjects")
public class SubjectService {

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
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

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postNewSubject(Subject ns, @Context UriInfo uriInfo) throws BadRequestException {
        Subject subject = Storage.addSubject(ns);

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(subject.getId()));
        return Response.created(builder.build()).entity(subject).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    @RolesAllowed({"admin", "supervisor"})
    public Response updateSubject(@PathParam("id") Long updatedSubjectId, Subject newSubject) throws NotFoundException {
        Subject subject = Storage.updateSubject(updatedSubjectId, newSubject);
//        course.clearLinks();
        return Response.ok(subject).build();
    }
}
