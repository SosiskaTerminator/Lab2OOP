import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Введите запрос (например, ЛЭТИ): ");
        String searchQuery = scan.nextLine();

        String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString());

        String urlString = "https://ru.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=" + encodedQuery;

        URL address = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) address.openConnection();
        connection.setRequestMethod("GET");

        connection.setRequestProperty("User-Agent", "WikiSearch/1.0");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                String jsonString = response.toString();

                System.out.println(jsonString);

                JsonObject root = JsonParser.parseString(response.toString()).getAsJsonObject();

                JsonObject query = root.getAsJsonObject("query");
                JsonArray search = query.getAsJsonArray("search");
                JsonObject article = search.get(0).getAsJsonObject();
                int pageid = article.get("pageid").getAsInt();

                String url = "https://ru.wikipedia.org/w/index.php?curid=" + pageid;

                Desktop desktop = Desktop.getDesktop();
                URI uri = new URI(url);

                desktop.browse(uri);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Cry baby. Код ответа сервера: " + responseCode);
        }

        connection.disconnect();
    }
}