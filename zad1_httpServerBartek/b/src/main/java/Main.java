import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class Main {
    private static String path = "src/";

    public static void main(String[] args) throws Exception {

        if (args.length > 0) {
            path = args[0];
        }
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ContentHandler());

        System.out.println("Starting server on port: " + port);
        System.out.println(path);
        server.start();
    }


    static class ContentHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                StringBuilder builder = new StringBuilder();

                File file = new File(path);
                String mime = Files.probeContentType(file.toPath());

                if (file.isDirectory() && file.canRead()) {
                    System.out.println("directory");
                    builder.append("<!DOCTYPE html>")
                            .append("<html>")
                            .append("<head>")
                            .append("<meta charset=\"UTF-8\">")
                            .append("<title>B</title>")
                            .append("</head>")
                            .append("<body>")
                            .append("<ul>");

                    for (File f : file.listFiles()) {
                        builder.append("<li>")
                                .append(path)
                                .append(path.endsWith("/") ? "" : "/")
                                .append(f.getName())
                                .append("")
                                .append("</li>");
                    }
                    builder.append("</ul>")
                            .append("</body>")
                            .append("</html>");

                    System.out.println(builder.toString());
                    System.out.println(mime);

                    exchange.getResponseHeaders().set("Content-Type", "");
                    exchange.sendResponseHeaders(200, builder.toString().getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(builder.toString().getBytes());
                    os.close();

                } else if (file.isFile() && file.canRead()) {
                    System.out.println("file");
                    System.out.println(mime);

                    FileInputStream stream = new FileInputStream(file.getAbsolutePath());

                    exchange.getResponseHeaders().set("Content-Type", mime);
                    exchange.sendResponseHeaders(200, IOUtils.readFully(stream, -1).length);

                    OutputStream os = exchange.getResponseBody();
                    os.write(IOUtils.readFully(stream, -1));
                    os.close();

                } else {
                    System.out.println("error");
                    exchange.getResponseHeaders().set("Content-Type", "text/plain");
                    exchange.sendResponseHeaders(404, "Not Found".length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(Integer.parseInt("Not Found"));
                    os.close();
                }

            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }
}
