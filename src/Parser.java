import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private String json;
    private JsonArray elements = new JsonArray();

    public Parser(InputStream stream) throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        String temp;
        StringBuilder pre_json = new StringBuilder();

        while ((temp = buffer.readLine()) != null) {
            pre_json.append(temp);
        }
        buffer.close();

        json = pre_json.toString();
        getAllElements();
    }

    private void getAllElements() throws IOException {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        if (root.has("error")) {
            JsonElement error = root.get("error");
            String info = error.isJsonObject() && error.getAsJsonObject().has("info")
                    ? error.getAsJsonObject().get("info").getAsString()
                    : error.toString();
            throw new IOException("Ошибка API: " + info);
        }

        JsonObject query = root.getAsJsonObject("query");
        if (query == null) {
            throw new IOException("В ответе сервера нет результатов поиска.");
        }

        JsonArray search = query.getAsJsonArray("search");
        if (search != null) elements = search;
    }

    public List<String> getResults() {
        List<String> temp = new ArrayList<>();

        for (int i = 0; i < elements.size(); i++) {
            JsonObject el = elements.get(i).getAsJsonObject();
            temp.add(el.get("title").toString());
        }

        return temp;
    }

    public String getPageId(int pos) {
        return elements.get(pos - 1).getAsJsonObject().get("pageid").toString();
    }
}
