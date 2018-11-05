package ph.adamw.calloji.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {
    private static final Gson gson = new Gson();
    private static final JsonParser parser = new JsonParser();

    public static JsonElement parseJson(String y) {
        return parser.parse(y);
    }

    public static JsonElement getJsonElement(Object o) {
        return gson.toJsonTree(o);
    }

    public static <T> T getObject(JsonElement element, Class<T> clazz) {
        return gson.fromJson(element, clazz);
    }
}
