package com.asia.services;

import com.asia.Main;
import com.asia.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/students")
public class StudentsService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Student> getAllStudents() {
        return Main.getDatabase().getStudents();
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewStudent(Student newStudent, @Context UriInfo uriInfo) throws BadRequestException {
        if (newStudent.getFirstName().length() > 0 && newStudent.getLastName().length() > 0 && newStudent.getBirthday().toString().length() > 0) {
            Student student = Main.getDatabase().addStudent(newStudent);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(student.getIndex()));
            return Response.created(builder.build()).entity(student).build();
        } else {
            return Response.noContent().status(400).build();
        }
    }



}
