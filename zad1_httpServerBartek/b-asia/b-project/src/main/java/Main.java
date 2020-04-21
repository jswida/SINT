import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;

public class Main {
    private static String defaultPath =  System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {

        //tylko dla ścieżek absolutnych - testowane na macOS
        if (args.length > 0) {
            defaultPath = args[0];
        }
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ContentHandler());
        server.start();
    }


    static class ContentHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                String canonical = "";
                String path = "/";
                Integer rc = 404;
                StringBuilder builder = new StringBuilder();
                String uri = exchange.getRequestURI().toString().replaceAll("%20", " ");
                uri = URLDecoder.decode(uri, "UTF-8");

                if (!uri.substring(1).equals("")) path = uri;
                File file = new File(defaultPath, path);

                String mimeType = null;
                try {
                    mimeType = Files.probeContentType(file.toPath());
                } catch (IOException e) {
                    System.out.println(e);
                }
                boolean correctPath = false;
                try {
                     canonical = file.getCanonicalPath();
                     correctPath = canonical.startsWith(defaultPath);
                } catch (IOException e) {
                    System.out.println(e);
                }

                // Return 403 error
                if (!correctPath) { rc = 403; }
                else if (!file.canRead() ) { rc = 404;}
                else {
                    rc = 200;
                    if (file.isDirectory()) {
                        builder.append(htmlHeader())
                                .append("<ul>")
                                .append("<li><a href=\"")
                                .append(path.startsWith("/") ? "" : "/")
                                .append(path)
                                .append(path.endsWith("/") ? "" : "/")
                                .append("../\">..</a></li>");

                        for (File f : file.listFiles()) {
                            if (!f.isHidden()) {
                                builder.append("<li>")
                                        .append("<a href=\"")
                                        .append(path.startsWith("/") ? "" : "/")
                                        .append(path)
                                        .append(path.endsWith("/") ? "" : "/")
                                        .append(f.getName())
                                        .append("\">")
                                        .append(f.getName())
                                        .append("</a>")
                                        .append("</li>");
                            }
                        }
                        builder.append(htmlFooter());

                    } else if (file.isFile()) {
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

                        }
                    }
                }
                if (!file.isFile()){
                    exchange.getResponseHeaders().set("Content-Type", "text/html");
                    exchange.sendResponseHeaders(rc, builder.toString().getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(builder.toString().getBytes());
                    os.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }

    }

    private static String htmlHeader() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Exercise 3</title>\n" +
                "</head>" +
                "<body>";
    }

    private static String htmlFooter() {
        return "</body>\n" +
                "</html>";
    }

}

