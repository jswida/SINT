package com.asia;

import com.asia.services.*;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;


import java.io.IOException;
import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8000/";

    public static void main(String[] args) throws IOException {
        DataBase data = new DataBase();
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));


        System.in.read();
        server.shutdown();
    }


    public static HttpServer startServer() {
        final ResourceConfig rc = new ResourceConfig(
                StudentService.class,
                StudentsService.class,
                GradeService.class,
                GradesService.class,
                CourseService.class,
                CoursesService.class,
                DeclarativeLinkingFeature.class,
                ExceptionHandler.class
        );

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

}
