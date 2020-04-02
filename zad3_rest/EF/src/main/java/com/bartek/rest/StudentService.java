package com.bartek.rest;

import com.bartek.Storage;
import com.bartek.models.Grade;
import com.bartek.models.Student;
import com.bartek.models.Subject;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashSet;
import java.util.Set;

@Path("/students")
public class StudentService {


    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Set<Student> getAllStudents(){
        return Storage.getStudents();
    }


    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Student getStudentById(@PathParam("id") long id){
        Student student = Storage.getStudents().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        if (student != null) return student;
        else throw new NotFoundException();
    }


    @GET
    @Path("/{id}/grades")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Set<Grade> getStudentGrades(@PathParam("id") long id){
        Student student = Storage.getStudents().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        if (student != null) return student.getGrades();
        else throw new NotFoundException();
    }


    @GET
    @Path("/{id}/grades/{gradeId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Grade getStudentGradeById(@PathParam("id") long id, @PathParam("gradeId") long gradeId){
        Student student = Storage.getStudents().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        if (student != null) {
            Grade grade = student.getGrades().stream().filter(s -> s.getId().equals(gradeId)).findFirst().orElse(null);
            if (grade != null) return grade;
            else throw new NotFoundException();
        }
        else throw new NotFoundException();
    }


    @GET
    @Path("/{id}/subjects")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Set<Subject> getStudentSubjects(@PathParam("id") long id){
        Student student = Storage.getStudents().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        if (student != null) {
            Set<Subject> studentSubjects = new HashSet<Subject>();
            for (Grade grade : student.getGrades()){
                Subject subject = Storage.getSubjects().stream().filter(s -> s.getId().equals(grade.getSubjectId())).findFirst().orElse(null);
                if (subject != null) studentSubjects.add(subject);
            }
            return studentSubjects;
        }
        else throw new NotFoundException();
    }


    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteStudent(@PathParam("id") long id) throws NotFoundException{

        System.out.println("delete @");
        Storage.delete(Student.class, id);
        return Response.noContent().build();
    }


    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    @RolesAllowed("admin")
    public Response postNewStudent(Student ns, @Context UriInfo uriInfo) throws BadRequestException {
        Student student = Storage.addStudent(ns);
//        student.clearLinks();

        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(Long.toString(student.getId()));
        return Response.created(builder.build()).entity(student).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
//    @RolesAllowed({"admin", "supervisor"})
    public Response updateStudent(@PathParam("id") Long updateStudentId, Student newStudent) throws NotFoundException {
        Student student = Storage.updateStudent(updateStudentId, newStudent);
//        course.clearLinks();
        return Response.ok(student).build();
    }

}
