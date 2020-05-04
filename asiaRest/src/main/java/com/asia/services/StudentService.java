package com.asia.services;

import com.asia.Main;
import com.asia.models.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashSet;
import java.util.Set;

@Path("/students/{index}")
public class StudentService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student getStudentById(@PathParam("index") long id) {
        Student student = Main.getDatabase().getStudent(id);
        if (student != null) return student;
        else throw new NotFoundException();
    }

    @GET
    @Path("/courses")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Course> getStudentCourses(@PathParam("index") long id) {
        Student student = Main.getDatabase().getStudent(id);
        if (student != null) {
            Set<Course> studentCourses = new HashSet<Course>();
            for (Grade grade : student.getGrades()) {
                Course course = Main.getDatabase().getCourse(grade.getCourse().getId());
                if (course != null) studentCourses.add(course);
            }
            return studentCourses;
        } else throw new NotFoundException();
    }


    @DELETE
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteStudent(@PathParam("index") long index) throws NotFoundException {
        Student student = Main.getDatabase().getStudent(index);
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
    public Response updateStudent(@PathParam("index") Long updateStudentIndex, Student newStudent) throws NotFoundException {
        Student student = Main.getDatabase().getStudent(updateStudentIndex);
        if (newStudent.getFirstName() != null && newStudent.getLastName() != null && newStudent.getBirthday() != null) {
            student = Model.getInstance().updateStudent(student, newStudent);
            return Response.ok(student).status(204).build();
        } else {
            return Response.ok(student).status(400).build();
        }
    }

}
