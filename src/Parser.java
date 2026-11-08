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
    private List<JsonElement> elements;

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

    private void getAllElements() {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        JsonObject query = root.getAsJsonObject("query");
        JsonArray search = query.getAsJsonArray("search");
        elements = search.asList();
    }
}
