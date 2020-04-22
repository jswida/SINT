package com.bartek.rest;

import com.bartek.Storage;
import com.bartek.models.Grade;
import com.bartek.models.GradeValue;
import com.bartek.models.Student;
import com.bartek.models.Course;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/students")
public class StudentService {


    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Student> getAllStudents() {
        return Storage.getStudents();
    }


    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Student getStudentById(@PathParam("id") long id) {
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) return student;
        else throw new NotFoundException();
    }


    @GET
    @Path("/{id}/grades")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Grade> getStudentGrades(@PathParam("id") long id) {
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) return student.getGrades();
        else throw new NotFoundException();
    }


    @GET
    @Path("/{id}/grades/{gradeId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getStudentGradeById(@PathParam("id") long id, @PathParam("gradeId") long gradeId) {
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) {
            Grade grade = student.getGrades().stream().filter(s -> s.getId().equals(gradeId)).findFirst().orElse(null);
            if (grade != null) {
                return Response.ok(grade).build();
            } else {
                return Response.status(404).build();
            }
        }else {
                return Response.noContent().status(404).build();
            }
    }


    @GET
    @Path("/{id}/courses")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Set<Course> getStudentCourses(@PathParam("id") long id) {
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex() == id).findFirst().orElse(null);
        if (student != null) {
            Set<Course> studentCourses = new HashSet<Course>();
            for (Grade grade : student.getGrades()) {
                Course course = Storage.getCourses().stream().filter(s -> s.getId().equals(grade.getCourse().getId())).findFirst().orElse(null);
                if (course != null) studentCourses.add(course);
            }
            return studentCourses;
        } else throw new NotFoundException();
    }


    @DELETE
    @Path("/{id}")
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

    @DELETE
    @Path("/{id}/grades/{gradeId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response deleteStudent(@PathParam("id") long id, @PathParam("gradeId") long gradeId) throws NotFoundException {
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex().equals(id)).findFirst().orElse(null);
        if (student != null) {
            Grade grade = student.getGrades().stream().filter(s -> s.getId().equals(gradeId)).findFirst().orElse(null);
            if (grade != null) {
                Storage.delete(Grade.class, gradeId);
                return Response.noContent().build();
            }
            else{
                return Response.noContent().status(404).build();
            }
        }
        else{
            return Response.noContent().status(404).build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewStudent(Student ns, @Context UriInfo uriInfo) throws BadRequestException {
        if (ns.getFirstName().length() > 0 && ns.getLastName().length() > 0 && ns.getBrithday().toString().length() > 0) {
            Student student = Storage.addStudent(ns);
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
    public Response postNewGradeForStudent(Grade ng, @PathParam("id") long id, @Context UriInfo uriInfo) throws BadRequestException {
//        System.out.println(ng.toString());
        List<Double> gradesList = Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0);
        if (gradesList.contains(ng.getValue()) && ng.getDate().toString().length() > 0 && ng.getCourse() != null){
            Grade grade = Storage.addGradeToStudent(ng, id);
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
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex().equals(updateStudentId)).findFirst().orElse(null);
        if (newStudent.getFirstName() != null && newStudent.getLastName() != null && newStudent.getBrithday() != null) {
            student = Storage.updateStudent(updateStudentId, newStudent);
            return Response.ok(student).status(204).build();
        } else {
            return Response.ok(student).status(400).build();
        }
    }

    @PUT
    @Path("/{id}/grades/{gradeId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateStudentGradeById(@PathParam("id") long id, @PathParam("gradeId") long gradeId, Grade newGrade) {

        List<Double> gradesList = Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0);
        Student student = Storage.getStudents().stream().filter(s -> s.getIndex().equals(id)).findFirst().orElse(null);
        if (student != null) {
            Grade grade = student.getGrades().stream().filter(s -> s.getId().equals(gradeId)).findFirst().orElse(null);
            if (grade != null) {
                if (gradesList.contains(newGrade.getValue())) {
                    grade = Storage.updateGrade(gradeId, newGrade);
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
