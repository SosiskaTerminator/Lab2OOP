import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Requester {
    private String request_encoded;
    private HttpURLConnection connection;

    public void setRequest(String to_find) {
        request_encoded = URLEncoder.encode(to_find, StandardCharsets.UTF_8);
    }

    public int makeConnection() {
        try {
            String url = "https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=";
            URL address = new URL(url + request_encoded);
            connection = (HttpURLConnection) address.openConnection();
            connection.setRequestProperty("User-Agent", "WikiSearch/1.0");

            return connection.getResponseCode();
        } catch (MalformedURLException e) {
            System.out.println("Something wrong with url.");
            return -1;
        } catch (IOException e) {
            return -1;
        }
    }

    public InputStream getConnectionResult() throws IOException { return connection.getInputStream(); }

    public void disconnect() { connection.disconnect(); }
}
