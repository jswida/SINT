import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;

/**
 * tested on ubuntu 18.04
 * ONLY ABSOLUTE PATH IN PARAMS
 *
 */

public class Main {
    // set for canonical path check in ubuntu
    private static String path = "/";
    private static String defaultPath =  System.getProperty("user.dir");
    private static String canonical = "";

    public static void main(String[] args) throws Exception {

        // ONLY ABSOLUTE PATH IN PARAMS
        if (args.length > 0) {
            defaultPath = args[0];
        }
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ContentHandler());

        System.out.println("Starting server on port: " + port);
        System.out.println("Default path: " + defaultPath);
        server.start();
    }


    static class ContentHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                StringBuilder builder = new StringBuilder();
                // get path from URI
                String fromURI = exchange.getRequestURI().toString().replaceAll("%20", " ");
                fromURI = URLDecoder.decode(fromURI, "UTF-8");

                System.out.println("from URL: " + fromURI);

//                if (!fromURI.substring(1).equals("")) path = fromURI; // incorrect condition
                path = fromURI;


                File file = new File(defaultPath, path);

//                System.out.println("path after modifications: " + path);
//                System.out.println("filepath: " + file.getPath());

                // path traversal
                boolean correctPath = false;
                try {
                    canonical = file.getCanonicalPath();
                    if (!canonical.endsWith("/")) canonical += "/";

                    System.out.println("canonical: \t" + canonical);
                    System.out.println("default: \t" + defaultPath);

                    correctPath = canonical.startsWith(defaultPath);
                } catch (IOException ignored) {
                }

                // get mime type
                String mimeType = null;
                try {
                    mimeType = Files.probeContentType(file.toPath());
                } catch (IOException ignored) {
                    errorMessage(exchange, 404);
                }

                // 403 error if path traversal attack
                if (!file.canRead() ) {
                    errorMessage(exchange, 404);
                }
                else if (!correctPath) {
                    System.out.println("PATH TRAVERSAL!");
                    errorMessage(exchange, 403);
                } else {

//                    System.out.println("file: " + file.toPath());
//                    System.out.println("mime: " + mimeType);

                    if (file.isDirectory()) {
//                        System.out.println("directory");
                        builder.append("<!DOCTYPE html>")
                                .append("<html>")
                                .append("<head>")
                                .append("<meta charset=\"UTF-8\">")
                                .append("<title>B</title>")
                                .append("</head>")
                                .append("<body>")
                                .append("<ul>")
                                .append("<li><a href=\"")
                                .append(path.startsWith("/") ? "" : "/")
                                .append(path)
                                .append(path.endsWith("/") ? "" : "/")
                                .append("../\">..</a></li>")
                        ;


                        for (File f : file.listFiles()) {
                            if (!f.isHidden()) { // dont list hidden files
                                builder.append("<li>")
                                        .append("<a href=\"")
                                        .append(path.startsWith("/") ? "" : "/")
                                        .append(path)
                                        .append(path.endsWith("/") ? "" : "/")
                                        .append(f.getName())
                                        .append("\">")
                                        .append(f.getName())
                                        .append("</a>")
                                        .append("</li>")
                                ;
                            }
                        }
                        builder.append("</ul>")
                                .append("</body>")
                                .append("</html>");

//                        System.out.println(builder.toString());

                        exchange.getResponseHeaders().set("Content-Type", "text/html");
                        exchange.sendResponseHeaders(200, builder.toString().getBytes().length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(builder.toString().getBytes());
                        os.close();

                    } else if (file.isFile()) {
//                        System.out.println("file");

                        byte[] bytes = new byte[(int) file.length()];
                        if (bytes != null) {

                            FileInputStream fileInputStream = new FileInputStream(file);
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

                            bufferedInputStream.read(bytes, 0, bytes.length);
                            if (mimeType != null) exchange.getResponseHeaders().set("Content-Type", mimeType);

                            exchange.sendResponseHeaders(200, file.length());

                            OutputStream os = exchange.getResponseBody();
                            os.write(bytes, 0, bytes.length);
                            os.close();

                        } else {
                            errorMessage(exchange, 404);
                        }

                    } else {
                        errorMessage(exchange, 404);
                    }

                }
            } catch (IOException e) {
//                System.out.println(e);
            }
        }

    }

    static public void errorMessage(HttpExchange exchange, int code) throws IOException {
        try {
            System.out.println("ERROR " + code);
            String error = "Not Found \n[invalid or illegal path]";
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(code, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();

        } catch (IOException e) {
            System.out.println(e);
        }
    }
}

