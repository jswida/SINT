import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BServer {
    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new EchoHandler());
        server.createContext("/echo", new EchoHandler());

        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class EchoHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                File file = new File("src\\index.html");
                byte[] fileContent = Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().set("Content-Type", "");
                exchange.sendResponseHeaders(200, fileContent.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileContent);
                os.close();

                Scanner in = new Scanner(System.in);
                System.out.println("Enter path:");
                String s = in.nextLine();
                System.out.println("You entered string "+ s);

                try (Stream<Path> walk = Files.walk(Paths.get(s))) {

                    List<String> result = walk.filter(Files::isDirectory)
                            .map(x -> x.toString()).collect(Collectors.toList());

                    result.forEach(System.out::println);

                } catch (IOException e) {
                    e.printStackTrace();
                }



            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }

    static class RedirectHandler implements HttpHandler {
        public static boolean isNumeric(String strNum) {
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

        public void handle(HttpExchange exchange) throws IOException {
            try {

                String red_code = exchange.getRequestURI().toString().split("/")[2];
                if (isNumeric(red_code)) {
                    int rc = Integer.parseInt(red_code);
                    Logger logger = Logger.getLogger("redlogger");
                    logger.log(Level.INFO, String.valueOf(red_code));

                    exchange.getResponseHeaders().set("Location", "https://google.com");
                    exchange.sendResponseHeaders(rc, -1);

                } else {

                    Logger logger = Logger.getLogger("redlogger");
                    logger.log(Level.INFO, "String.valueOf(red_code)");

                    exchange.getResponseHeaders().set("Location", "/");
                    exchange.sendResponseHeaders(303, -1);

                }

            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }

    static class CookiesHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                Random ran = new Random();
                int x = ran.nextInt(1000) + 100;
                String cookie = String.valueOf(x);

                exchange.getResponseHeaders().set("Set-Cookie", cookie);
                exchange.sendResponseHeaders(200, cookie.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(cookie.getBytes());
                os.close();

            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }

    static class BasicAuthenticationHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String login = "student";
            String password = "student";
            int rc = 401;
            String out = "NOT LOGGED IN";
            String auth = exchange.getRequestHeaders().getFirst("Authorization");

            String[] parts = auth.split("\\s");
            if (parts[0].equals("Basic") && parts.length > 1) {
                Base64.Decoder decoder = Base64.getDecoder();
                String decoded = new String(decoder.decode(parts[1]));
                String[] credentials = decoded.split(":");

                if (credentials.length == 2) {
                    if (credentials[0].equals(login) && credentials[1].equals(password)) {
                        rc = 200;
                        out = "OK, LOGGED IN";
                    }
                }
            }

            System.out.println("work well");
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=MyDomain");
            exchange.sendResponseHeaders(rc, out.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(out.getBytes());
            os.close();
            System.out.println("finished");
        }
    }

    static class BasicAuthentication2Handler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String out = "OK, LOGGED IN";
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(200, out.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(out.getBytes());
            os.close();
        }
    }
}