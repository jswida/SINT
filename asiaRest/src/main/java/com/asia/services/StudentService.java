package com.asia.services;

import com.asia.DataBase;
import com.asia.models.Grade;
import com.asia.models.Student;
import com.asia.models.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashSet;
import java.util.Set;

@Path("/students")
public class StudentService {
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Student> getAllStudents() {
        return DataBase.getStudents();
    }


    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student getStudentById(@PathParam("id") long id) {
        Student student = DataBase.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) return student;
        else throw new NotFoundException();
    }


    @GET
    @Path("/{id}/grades")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Grade> getStudentGrades(@PathParam("id") long id) {
        Student student = DataBase.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) return student.getGrades();
        else throw new NotFoundException();
    }


    @GET
    @Path("/{id}/grades/{gradeId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Grade getStudentGradeById(@PathParam("id") long id, @PathParam("gradeId") long gradeId) {
        Student student = DataBase.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) {
            Grade grade = student.getGrades().stream().filter(s -> s.getId().equals(gradeId)).findFirst().orElse(null);
            if (grade != null) return grade;
            else throw new NotFoundException();
        } else throw new NotFoundException();
    }


    @GET
    @Path("/{id}/courses")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Course> getStudentCourses(@PathParam("id") long id) {
        Student student = DataBase.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) {
            Set<Course> studentCourses = new HashSet<Course>();
            for (Grade grade : student.getGrades()) {
                Course course = DataBase.getCourses().stream().filter(s -> s.getId().equals(grade.getCourse().getId())).findFirst().orElse(null);
                if (course != null) studentCourses.add(course);
            }
            return studentCourses;
        } else throw new NotFoundException();
    }


    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteStudent(@PathParam("id") long id) throws NotFoundException {

        System.out.println("delete @");
        DataBase.delete(Student.class, id);
        return Response.noContent().build();
    }


    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewStudent(Student ns, @Context UriInfo uriInfo) throws BadRequestException {
        if (ns.getFirstName().length() > 0 && ns.getLastName().length() > 0 && ns.getBrithday().toString().length() > 0) {
            Student student = DataBase.addStudent(ns);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(student.getIndex()));
            return Response.created(builder.build()).entity(student).build();
        } else {
            return Response.noContent().status(400).build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/{id}/grades")
    public Response postNewGradeForStudent(Grade newGrade, @PathParam("id") long id, @Context UriInfo uriInfo) throws BadRequestException {
//        System.out.println(newGrade);
//        System.out.println(id);
        if (newGrade.getValue().toString().length() > 0 && newGrade.getDate().toString().length() > 0 && newGrade.getCourse() != null){
            Grade grade = DataBase.addGradeToStudent(newGrade, id);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(grade.getId()));
            return Response.created(builder.build()).entity(grade).build();
        }
        else{
            return Response.noContent().status(400).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateStudent(@PathParam("id") Long updateStudentId, Student newStudent) throws NotFoundException {
        Student student = DataBase.getStudents().stream().filter(s -> s.getIndex().equals(updateStudentId)).findFirst().orElse(null);
        if (newStudent.getFirstName() != null && newStudent.getLastName() != null && newStudent.getBrithday() != null) {
            student = DataBase.updateStudent(updateStudentId, newStudent);
            return Response.ok(student).status(204).build();
        } else {
            return Response.ok(student).status(400).build();
        }
    }

    @PUT
    @Path("/{id}/grades/{gradeId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateStudentGradeById(@PathParam("id") long id, @PathParam("gradeId") long gradeId, Grade newGrade)
        {
            Student student = DataBase.getStudents().stream().filter(s -> s.getIndex().equals(id)).findFirst().orElse(null);
            System.out.println(newGrade);
            if (student != null) {
                Grade grade = student.getGrades().stream().filter(s -> s.getId().equals(gradeId)).findFirst().orElse(null);
                if (grade != null && newGrade != null) {
                    if (newGrade.getValue().getValue() >= 2.0 && newGrade.getValue().getValue() <= 5.0) {
                        System.out.println(newGrade.getValue());
                        System.out.println(newGrade.getValue());
                        grade = DataBase.updateGrade(gradeId, newGrade);
                        return Response.ok(grade).status(204).build();
                    } else {
                        return Response.noContent().status(400).build();
                    }
                } else {
                    return Response.noContent().status(400).build();
                }
            } else {
                return Response.noContent().status(400).build();
            }
        }

}

