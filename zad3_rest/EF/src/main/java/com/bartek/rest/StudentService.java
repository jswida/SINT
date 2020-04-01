package com.bartek.rest;

import com.bartek.Storage;
import com.bartek.models.Grade;
import com.bartek.models.Student;
import com.bartek.models.Subject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
                studentSubjects.add(grade.getSubject());
            }
            return studentSubjects;
        }
        else throw new NotFoundException();
    }

    @GET
    @Path("/{id}/subjects/{subjectId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Subject getStudentSubjectById(@PathParam("id") long id, @PathParam("subjectId") long subjectId){
        Student student = Storage.getStudents().stream().filter(s -> s.getId() == id).findFirst().orElse(null);
        if (student != null) {
            Set<Subject> studentSubjects = new HashSet<Subject>();
            for (Grade grade : student.getGrades()){
                studentSubjects.add(grade.getSubject());
            }
            Subject subject = studentSubjects.stream().filter(s -> s.getId() == subjectId).findFirst().orElse(null);
            if (subject != null) return subject;
            else throw new NotFoundException();
        }
        else throw new NotFoundException();
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void deleteStudent(@PathParam("id") long id){
        Storage.delete(Student.class, id);
    }
}
