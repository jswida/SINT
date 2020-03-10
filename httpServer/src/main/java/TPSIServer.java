import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
            try {
                String response = "Hello World!";
                byte[] array = Files.readAllBytes(Paths.get("src/index.html"));
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                exchange.sendResponseHeaders(200, array.length);
                OutputStream os = exchange.getResponseBody();
                os.write(array);
                os.close();
            } catch (Throwable error) {
                error.printStackTrace();
            }
        }
    }

    // A2 - handlers

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    static class EchoHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Headers headers = exchange.getRequestHeaders();
                String json = JsonWriter.objectToJson(headers);


                byte[] array = JsonWriter.formatJson(json).getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(200, array.length);
                OutputStream os = exchange.getResponseBody();
                os.write(array);
                os.close();
            } catch (Throwable error) {
                error.printStackTrace();
            }
        }
    }

        static class RedirectHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String REDIRECT_CODE = exchange.getRequestURI().toString().split("/")[2];

                if(isNumeric(REDIRECT_CODE)){
                    int rc = Integer.parseInt(REDIRECT_CODE);
                    Logger logger = Logger.getLogger("RedirectLogger");
                    logger.info(exchange.getRequestMethod() + " " + exchange.getRequestURI().toString());
                    exchange.getResponseHeaders().set("Location", "http://google.com");
                    exchange.sendResponseHeaders(rc, -1);
                }
                else{

                    exchange.getResponseHeaders().set("Location", "/");
                    exchange.sendResponseHeaders(303, -1);
                }
            }
        }

        static class CookiesHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange exchange) throws IOException {
                int cookie = (int) (Math.random() * 100 + 1);
                String cookieVal = String.valueOf(cookie);

                exchange.getResponseHeaders().set("Set-Cookie", cookieVal);
                exchange.sendResponseHeaders(200, cookieVal.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(cookieVal.getBytes());
                os.close();

            }
        }

        static class AuthHandler implements HttpHandler {

            @Override
            public void handle(HttpExchange exchange) throws IOException {

            }
        }


    }
