package com.asia;

import com.asia.services.GradeService;
import com.asia.services.StudentService;
import com.asia.services.CourseService;
import com.asia.services.ExceptionHandler;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
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


        System.out.println();
        System.in.read();
        server.shutdown();
    }


    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
        final ResourceConfig rc = new ResourceConfig(
                StudentService.class,
                GradeService.class,
                CourseService.class,
                ExceptionHandler.class
        );

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

}
