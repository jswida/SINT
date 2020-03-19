import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyServer {

    private static String proxylistFile = "proxyfile.txt";

    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ProxyHandler());

        ArrayList<String> proxylist = readList();

        System.out.println("Starting server on port: " + port);
        server.start();
    }


    static class ProxyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            try {
                /* REQUEST */
                // get request info
                String method = exchange.getRequestMethod();
                InputStream bodyInputStream = exchange.getRequestBody();
                Set<Map.Entry<String, List<String>>> headers = exchange.getRequestHeaders().entrySet();
                URL url = exchange.getRequestURI().toURL();

                // create logger
                Logger logger = Logger.getLogger("ProLogger");
                logger.info(exchange.getRequestMethod() + " " + exchange.getRequestURI().toString());

                // httpURLConnection connection
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                // necessary to disable follow redirects?
                connection.setInstanceFollowRedirects(false);
                HttpURLConnection.setFollowRedirects(false);

                // set headers
                for (Map.Entry<String, List<String>> header : headers) {
                    connection.addRequestProperty(header.getKey(), String.join(", ", header.getValue()));
                }

                // if method is post or put
                if (method.startsWith("P")) {
                    connection.setDoOutput(true);
                    OutputStream os = connection.getOutputStream();
                    byte[] bytes = bodyInputStream.readAllBytes();
//                    BufferedInputStream bufferedInputStream = new BufferedInputStream(bodyInputStream);
//                    bufferedInputStream.read(bytes, 0, bytes.length);
//                    transferedBytes = bytes.length;
                    os.write(bytes);
                }


                /* RESPONSE */
                // get responce info
                int codeCon = connection.getResponseCode();
                Headers headersResponse = exchange.getResponseHeaders();
                Set<Map.Entry<String, List<String>>> headersCon = connection.getHeaderFields().entrySet();
                byte[] responseCon = {};

                // get resposne
                try {
                    responseCon = connection.getInputStream().readAllBytes();
                } catch (Exception e) {
                    if (connection.getErrorStream() != null) responseCon = connection.getErrorStream().readAllBytes();
                }

                // Add statistics
//                addStatistics(new Statistic(url.getAuthority(), 0, response.length, 1));

                // set new headers
                for (Map.Entry<String, List<String>> header : headersCon) {
                    if (header.getKey() != null && !header.getKey().toLowerCase().equals("transfer-encoding")) {
                        headersResponse.add(header.getKey(), String.join(", ", header.getValue()));
                    }
                }

                /* SEND RESPONSE */
                exchange.sendResponseHeaders(codeCon, responseCon.length);
                OutputStream os = exchange.getResponseBody();
                if (responseCon.length > 0) os.write(responseCon);
                os.close();


            } catch (IOException e) { // 403 error
                System.out.println(e);
                errorMessage(exchange, 403);
            }

        }


        static public void errorMessage(HttpExchange exchange, int code) throws IOException {
            try {
                System.out.println("ERROR " + code);
                String error = "Forbidden \n[invalid or illegal path]";
//                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(code, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();

            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }


    private static ArrayList<String> readList() throws Exception {
        ArrayList<String> proxylist = new ArrayList<>();
        Scanner scanner = new Scanner(new File(proxylistFile));
        while (scanner.hasNext()) {
            proxylist.add(scanner.nextLine().toLowerCase());
        }

        return proxylist;
    }


}
