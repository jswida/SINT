package com.bartek.rest;

import com.bartek.Storage;
import com.bartek.models.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("/students")
public class StudentsService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Student> getAllStudents() {
        return Storage.getStudents();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewStudent(Student ns, @Context UriInfo uriInfo) throws BadRequestException {
        if (ns.getFirstName().length() > 0 && ns.getLastName().length() > 0 && ns.getBirthday().toString().length() > 0) {
            Student student = Storage.addStudent(ns);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(student.getIndex()));
            return Response.created(builder.build()).entity(student).build();
        } else {
            return Response.noContent().status(400).build();
        }
    }



}
