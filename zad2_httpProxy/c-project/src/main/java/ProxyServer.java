import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.sun.net.httpserver.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * examples sites:
 * http://open-up.eu/en
 * http://weevil.info/
 * http://africhthy.org/en - in proxylist to block
 */
public class ProxyServer {

    private static String proxylistFile = "proxylist.txt";
    private static String statslistFile = "stats.csv";
    private static List<StatsOnly> stats = new ArrayList<>();
    private static Semaphore semaphore = new Semaphore(1);
    private static int memoryBufferSize = 100;

    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        setStats();
        server.createContext("/", new ProxyHandler());

        // save when application is closing
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("ShutdownHook is running");
                try {
                    saveStats();
                    System.out.println("Stats saved");
                } catch (IOException e) {
                    System.out.println("Stats NOT saved");
                    e.printStackTrace();
                } catch (CsvValidationException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

        System.out.println("Starting server on port: " + port);
        server.start();
    }


    static class ProxyHandler implements HttpHandler {
        private ArrayList<String> proxylist;

        // simple constructor
        public ProxyHandler() throws FileNotFoundException {
            this.proxylist = readList();
            System.out.println("Proxy List: " + proxylist);
        }

        public void handle(HttpExchange exchange) {
            try {
//                System.out.println("handle() ---");
                /* REQUEST */
                // get request info
                String method = exchange.getRequestMethod();
                InputStream bodyInputStream = exchange.getRequestBody();
                Set<Map.Entry<String, List<String>>> headers = exchange.getRequestHeaders().entrySet();
                URL url = new URL(exchange.getRequestURI().toString()); //.toURL();

                // create logger
                Logger logger = Logger.getLogger("ProLogger");
                logger.info(exchange.getRequestMethod() + " " + exchange.getRequestURI().toString());

                // check if in the forbidden list
                for (String website : proxylist) {
                    if (url.toString().toLowerCase().contains(website)) {
                        errorMessage(exchange, 403);
//                        throw new IllegalAccessException();
                    }
                }

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

                int bytes_length = 0;
                // if method is post or put
                if (method.startsWith("P")) {
                    connection.setDoOutput(true);
                    OutputStream os = connection.getOutputStream();
                    byte[] bytes = bodyInputStream.readAllBytes();
                    bytes_length = bytes.length;
                    os.write(bytes);
                }

                StatsOnly newstat = new StatsOnly(url.getAuthority(), bytes_length, 0, 1);
                addStats(newstat);

                /* RESPONSE */
                // get response info
                int codeCon = connection.getResponseCode();

//                Headers headersResponse = exchange.getResponseHeaders();
                Set<Map.Entry<String, List<String>>> headersCon = connection.getHeaderFields().entrySet();
                byte[] responseCon = new byte[0];

                // get response
                try {
                    responseCon = connection.getInputStream().readAllBytes();
                } catch (Exception e) {
                    if (connection.getErrorStream() != null) responseCon = connection.getErrorStream().readAllBytes();
                }

                StatsOnly newstat2 = new StatsOnly(url.getAuthority(), 0, responseCon.length, 1);
                addStats(newstat2);

                // set new headers resolving transfer-encodinig problems
                for (Map.Entry<String, List<String>> header : headersCon) {
                    if (header.getKey() != null && !header.getKey().toLowerCase().equals("transfer-encoding")) {
                        exchange.getResponseHeaders().add(header.getKey(), String.join(", ", header.getValue()));
                    }
                }

                exchange.getResponseHeaders().add("Via", String.join(", ", exchange.getProtocol(), "localhost"));


                /* SEND RESPONSE */
                // code 304 == Not Modified
                if (codeCon == 304) exchange.sendResponseHeaders(codeCon, -1);
                else {
                    exchange.sendResponseHeaders(codeCon, responseCon.length);
                    OutputStream os = exchange.getResponseBody();
                    if (responseCon.length > 0) os.write(responseCon);
                    os.close();
                }

//                System.out.println("--- handle()");

            } catch (IOException | InterruptedException | CsvValidationException e) { // 403 error
                System.out.println("handle() exception");
//                e.printStackTrace();
            }

        }


        static public void errorMessage(HttpExchange exchange, int code) throws IOException {
            try {
                System.out.println("ERROR " + code);
                String error = "Forbidden \n[site is on forbidden proxy list or invalid / illegal path]";
//                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.sendResponseHeaders(code, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();

            } catch (IOException e) {
                System.out.println("Problem with generating error message");
                System.out.println(e);
            }
        }
    }


    private static ArrayList<String> readList() throws FileNotFoundException {
        ArrayList<String> proxylist = new ArrayList<>();
        Scanner scanner = new Scanner(new File(proxylistFile));
        while (scanner.hasNext()) {
            proxylist.add(scanner.nextLine().toLowerCase());
        }

        return proxylist;
    }

    public static String getProxylistFile() {
        return proxylistFile;
    }

    public static void setProxylistFile(String proxylistFile) {
        ProxyServer.proxylistFile = proxylistFile;
    }

    private static void setStats() throws IOException, CsvValidationException, InterruptedException {
        String[] next;

        File file = new File(statslistFile);
        FileReader fileReader = new FileReader(file);
        CSVReader reader = new CSVReader(fileReader);

        reader.readNext();
        while ((next = reader.readNext()) != null) {
            StatsOnly stat = new StatsOnly(next[0], Integer.parseInt(next[1]), Integer.parseInt(next[2]), Integer.parseInt(next[3]));
            addStats(stat);
        }
        System.out.println("stats.csv has been loaded");

    }

    public static List<StatsOnly> getStats() {
        return stats;
    }

    public static void addStats(StatsOnly stat) throws InterruptedException, IOException, CsvValidationException {
        semaphore.acquire();
        int statsLength = stats.size();
        try {
            stats.add(stat);
        } catch (Exception e){
            System.out.println("Adding to Stats error:");
            System.out.println(e);
        }
        semaphore.release();

        if (statsLength > memoryBufferSize){
            semaphore.acquire();
            tempSave();
            stats.clear();
            semaphore.release();
        }

    }

    private static void saveStats() throws IOException, CsvValidationException, InterruptedException {
        String[] next;
//        setStats();

//        File file = new File(statslistFile);
//        FileReader fileReader = new FileReader(file);
//        FileWriter fileWriter = new FileWriter(file);
        CSVWriter writer = new CSVWriter(new FileWriter(new File(statslistFile)));
//        CSVReader reader = new CSVReader(new FileReader(new File(statslistFile)));

        // read
//        reader.readNext();
//        while ((next = reader.readNext()) != null) {
//            System.out.println("reader+");
//            StatsOnly stat = new StatsOnly(next[0], Integer.parseInt(next[1]), Integer.parseInt(next[2]), Integer.parseInt(next[3]));
//            stats.add(stat);
//        }
//        reader.close();

        // load from serialized object
        ArrayList<StatsOnly> listBefore = new ArrayList<>();
        listBefore = (ArrayList<StatsOnly>) deserialize();
        stats.addAll(listBefore);

        // clear serialized object
        ArrayList<StatsOnly> emptylist = new ArrayList<>();
        serialize(emptylist);

        // write
        String[] header = {"domain", "sent", "received", "requests"};
        writer.writeNext(header, false);

        Map<String, List<StatsOnly>> collect = ProxyServer.getStats()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                StatsOnly::getWebsite, Collectors.toList()
                        )
                );

        for (Map.Entry<String, List<StatsOnly>> entries : collect.entrySet()) {
            ArrayList<String> response = new ArrayList<>();
            int receivedBytes = 0;
            int sentBytes = 0;
            int requests = 0;

            for (StatsOnly s : entries.getValue()) {
                receivedBytes += s.getReceived();
                sentBytes += s.getSent();
                requests += s.getRequest();
            }

            // make response
            StatsOnly statistic = entries.getValue().get(0);
            response.add(statistic.getWebsite());
            response.add(String.valueOf(sentBytes));
            response.add(String.valueOf(receivedBytes));
            response.add(String.valueOf(requests));

            // write to csv
            writer.writeNext(response.toArray(new String[0]), false);
        }

//        stats.clear();
//        collect.clear();

        writer.close();

    }

    public static void tempSave(){
        System.out.println("temp save");
        ArrayList<StatsOnly> listBefore = new ArrayList<>();

        listBefore = (ArrayList<StatsOnly>) deserialize();
        listBefore.addAll(stats);

        System.out.println("saved object size: " + listBefore.size());
        serialize(listBefore);

    }
    public static void serialize(ArrayList<StatsOnly> statslist){
        try
        {
            FileOutputStream fos = new FileOutputStream("statsSerialized");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(statslist);
            oos.close();
            fos.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public static List<StatsOnly> deserialize(){
        ArrayList<StatsOnly> statlist = new ArrayList<>();

        try
        {
            FileInputStream fis = new FileInputStream("statsSerialized");
            ObjectInputStream ois = new ObjectInputStream(fis);

            statlist = (ArrayList<StatsOnly>) ois.readObject();

            ois.close();
            fis.close();
        }
        catch (IOException ioe)
        {
            System.out.println("File not created yet");
//            ioe.printStackTrace();
            return statlist;
        }
        catch (ClassNotFoundException c)
        {
            System.out.println("Class not found");
//            c.printStackTrace();
            return statlist;
        }

        return statlist;
    }

}


