package com.bartek.rest;

import com.bartek.Main;
import com.bartek.models.Student;
import com.bartek.nosql.DateParamConverterProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Date;
import java.util.List;

@Path("/students")
public class StudentsService {

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Student> getAllStudents(@QueryParam("birthday") String date, @QueryParam("birthdayCompare") String birthdayCompare, @QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {
        try {
            Date birthday = new DateParamConverterProvider("yyyy-MM-dd").getConverter(Date.class, Date.class, null).fromString(date);
            return Main.getDatabase().getStudentsFiltered(firstName, lastName, birthday, birthdayCompare);
        }catch(BadRequestException e){
            Date birthday = null;
            return Main.getDatabase().getStudentsFiltered(firstName, lastName, birthday, birthdayCompare);
        }
            }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response postNewStudent(Student ns, @Context UriInfo uriInfo) throws BadRequestException {
        if (ns.getFirstName().length() > 0 && ns.getLastName().length() > 0 && ns.getBirthday().toString().length() > 0) {
            Student student = Main.getDatabase().addStudent(ns);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();
            builder.path(Long.toString(student.getIndex()));
            return Response.created(builder.build()).entity(student).build();
        } else {
            return Response.noContent().status(400).build();
        }
    }



}
