import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Scanner;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

public class Main {
    public static void main(String[] args) throws IOException {
        Requester req = new Requester();
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.print("Введите запрос : ");
            String searchQuery = scan.nextLine();

            req.setRequest(searchQuery);
            int response_code = req.makeConnection();
            if (response_code == 200) {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(req.getConnectionResult(), "UTF-8"))) {

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
                System.out.println("Cry baby. Код ответа сервера: " + response_code);
            }

            req.disconnect();
        }
    }
}