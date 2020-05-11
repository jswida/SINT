package com.bartek.rest;

import com.bartek.Storage;
import com.bartek.models.Course;
import com.bartek.models.Grade;
import com.bartek.models.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/students/{id}")
public class StudentService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student getStudentById(@PathParam("id") long id) {
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) return student;
        else throw new NotFoundException();
    }


    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteStudent(@PathParam("id") long id) throws NotFoundException {
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex().equals(id)).findFirst().orElse(null);
        if (student != null) {
            Storage.delete(Student.class, id);
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
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex().equals(updateStudentId)).findFirst().orElse(null);
        if (newStudent.getFirstName() != null && newStudent.getLastName() != null && newStudent.getBirthday() != null) {
            student = Storage.updateStudent(updateStudentId, newStudent);
            return Response.ok(student).status(204).build();
        } else {
            return Response.ok(student).status(400).build();
        }
    }




}
