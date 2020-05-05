package com.bartek.rest;

import com.bartek.Mango;
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
        Student student = Mango.getMangoIns().getStudent(id);
        if (student != null) return student;
        else throw new NotFoundException();
    }

    @GET
    @Path("/courses")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Course> getStudentCourses(@PathParam("id") long id) {
        Student student = Mango.getMangoIns().getStudent(id);
        if (student != null) {
            Set<Course> studentCourses = new HashSet<Course>();
            for (Grade grade : student.getGrades()) {
                Course course = Mango.getMangoIns().getCourses().stream().filter(s -> s.getId().equals(grade.getCourse().getId())).findFirst().orElse(null);
                if (course != null) studentCourses.add(course);
            }
            return studentCourses;
        } else throw new NotFoundException();
    }


    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteStudent(@PathParam("id") long id) throws NotFoundException {
        Student student = Mango.getMangoIns().getStudent(id);
        if (student != null) {
            Mango.getMangoIns().deleteStudent(student);
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
        Student student = Mango.getMangoIns().getStudent(updateStudentId);
        if (newStudent.getFirstName() != null && newStudent.getLastName() != null && newStudent.getBirthday() != null) {
            student = Mango.getMangoIns().updateStudent(student, newStudent);
            return Response.ok(student).status(204).build();
        } else {
            return Response.ok(student).status(400).build();
        }
    }




}
