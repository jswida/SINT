import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;



public class TPSIServer {
    private static String LOGIN = "student";
    private static String PASS = "student";

    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/echo", new EchoHandler());
        server.createContext("/cookies", new CookiesHandler());
        server.createContext("/auth", new BasicAuthenticationHandler());
        server.createContext("/redirect/", new RedirectHandler());
        HttpContext context = server.createContext("/auth2", new BasicAuthentication2Handler());

        context.setAuthenticator(new BasicAuthenticator("auth2") {
            @Override
            public boolean checkCredentials(String login, String password) {
                return login.equals(LOGIN) && password.equals(PASS);
            }
        });

        System.out.println("Starting server on port: " + port);
        server.start();
    }

    static class RootHandler implements HttpHandler {
        private String readFromInputStream(InputStream inputStream)
                throws IOException {
            StringBuilder resultStringBuilder = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    resultStringBuilder.append(line);
                }
            }
            return resultStringBuilder.toString();
        }

        public void handle(HttpExchange exchange) throws IOException {
            try {

                File file = new File(System.getProperty("user.dir") + "/index.html");
                byte[] fileContent = Files.readAllBytes(file.toPath());

                exchange.getResponseHeaders().set("Content-Type", "");
                exchange.sendResponseHeaders(200, fileContent.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileContent);
                os.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    static class EchoHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {

                Headers headers = exchange.getRequestHeaders();
                String json = JsonWriter.objectToJson(headers);
                byte[] array = JsonWriter.formatJson(json).getBytes();
//                System.out.println(json);

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, array.length);

                OutputStream os = exchange.getResponseBody();
                os.write(array);
                os.close();

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

                String domain = exchange.getRequestHeaders().getFirst("Host");
                System.out.println(domain);

                exchange.getResponseHeaders().add("Content-Type", "text/plain");
//                exchange.getResponseHeaders().set("Set-Cookie", cookie);
                exchange.getResponseHeaders().add("Set-Cookie", "CID=" + cookie + "; Domain=localhost;" + "Path=/");
                exchange.getResponseHeaders().add("Set-Cookie", "Echo=" + cookie + "; Domain=localhost;" + "Path=/echo");
                exchange.getResponseHeaders().add("Set-Cookie", "InvalidDomain=cookie; domain=google.pl");
                exchange.getResponseHeaders().add("Set-Cookie", "ValidDomain=cookie" + "; domain=" + domain);



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
            int rc = 401;
            String out = "NOT LOGGED IN";
            String auth = exchange.getRequestHeaders().getFirst("Authorization");

            if (auth != null) {
                String[] parts = auth.split("\\s");
                if (parts[0].equals("Basic") && parts.length > 1) {
                    Base64.Decoder decoder = Base64.getDecoder();
                    String decoded = new String(decoder.decode(parts[1]));
                    String[] credentials = decoded.split(":");

                    if (credentials.length == 2) {
                        if (credentials[0].equals(LOGIN) && credentials[1].equals(PASS)) {
                            rc = 200;
                            out = "OK, LOGGED IN";
                        }
                    }
                }
            }
            else {
                rc = 401;
                out = "EMPTY AUTH HEADER, NOT LOGGED IN";
            }

            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.getResponseHeaders().set("WWW-Authenticate", "Basic realm=MyDomain");
            exchange.sendResponseHeaders(rc, out.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(out.getBytes());
            os.close();
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
