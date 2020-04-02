package com.bartek;

import com.bartek.models.Grade;
import com.bartek.models.GradeValue;
import com.bartek.models.Student;
import com.bartek.models.Subject;
import com.bartek.rest.GradeService;
import com.bartek.rest.StudentService;
import com.bartek.rest.SubjectService;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

//TODO
// 2. dodać put - każdy parametr
// 4. przetestować w postmanie

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


    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example.rest package
        final ResourceConfig rc = new ResourceConfig(
                StudentService.class,
                GradeService.class,
                SubjectService.class
        );

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }


}
