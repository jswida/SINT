package com.bartek;

import com.bartek.nosql.Mango;
import com.bartek.rest.*;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;


public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8000/";

    public static void main(String[] args) throws IOException {
        Storage db = new Storage();
        
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        System.in.read();
        server.shutdown();
    }
    public static Mango getDatabase() {
        return Mango.getInstance();
    }


    public static HttpServer startServer() {
        ResourceConfig config = new ResourceConfig(
                StudentService.class,
                StudentsService.class,
                GradeService.class,
                GradesService.class,
                CourseService.class,
                CoursesService.class,
                DeclarativeLinkingFeature.class,
                ExceptionHandler.class
        );

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);
    }


}
