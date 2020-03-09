import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class TPSIServer {
    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // add new contexts - A2
        server.createContext("/", new RootHandler());
        server.createContext("/echo/", new EchoHandler());
        server.createContext("/redirect/", new RedirectHandler());
        server.createContext("/cookies/", new CookiesHandler());
        server.createContext("/auth", new AuthHandler());

        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            //A1 EXERCISES
            try{
                String response = "Hello World!";
                byte[] array = Files.readAllBytes(Paths.get("src/index.html"));
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, array.length);
                OutputStream os = exchange.getResponseBody();
                os.write(array);
                os.close();
            } catch (Throwable error){
                error.printStackTrace();
            }
        }
    }

    // A2 - handlers

    static class EchoHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder stringBuilder = new StringBuilder("{");
            String prefix = "";

            Headers headers = exchange.getResponseHeaders();
            for (Map.Entry<String, List<String>> header : headers.entrySet()) {
                stringBuilder.append(prefix);

//                String key = JsonWriter
            }

        }
    }

    static class RedirectHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Empty / redirect / code
            int REDIRECT_CODE = Integer.parseInt(exchange.getRequestURI().toString().split("/")[2]);

            Logger logger = Logger.getLogger("RedirectLogger");
            logger.info(exchange.getRequestMethod() + " " + exchange.getRequestURI().toString());
            exchange.getResponseHeaders().set("Location", "/");
            exchange.sendResponseHeaders(REDIRECT_CODE, -1);
        }
    }

    static class CookiesHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // create random cookie
            int cookie = (int) (Math.random() * 1000000 + 1);
            String cookieVal = Integer.toString(cookie);

            // to send cookies for diffrent uri
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, cookieVal.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(cookieVal.getBytes());
            os.close();

            // wrong domain atribute - cookie ignored

            // change cookies name - writing new cookie in cookies folder

        }
    }

    static class AuthHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {

        }
    }



}