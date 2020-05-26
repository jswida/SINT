package com.asia.services;

import com.asia.Main;
import com.asia.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashSet;
import java.util.Set;

@Path("/students/{id}")
public class StudentService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student getStudentById(@PathParam("id") long id) {
        Student student = Main.getDatabase().getStudentByID(id);
        if (student != null) return student;
        else throw new NotFoundException();
    }


    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteStudent(@PathParam("id") long id) throws NotFoundException {
        Student student = Main.getDatabase().getStudentByID(id);
        if (student != null) {
            Main.getDatabase().deleteStudent(student);
            return Response.noContent().build();
        }
        else{
            return Response.noContent().status(404).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateStudent(@PathParam("id") Long updateStudentId, Student newStudent) throws NotFoundException {
        Student student = Main.getDatabase().getStudentByID(updateStudentId);
        if (newStudent.getFirstName() != null && newStudent.getLastName() != null && newStudent.getBirthday() != null) {
            student = Main.getDatabase().updateStudent(student, newStudent);
            return Response.ok(student).status(204).build();
        } else {
            return Response.ok(student).status(400).build();
        }
    }
}
