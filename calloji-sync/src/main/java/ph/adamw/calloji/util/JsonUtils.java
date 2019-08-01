package ph.adamw.calloji.util;

import com.google.gson.*;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

@Log4j2
public class JsonUtils {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final JsonParser parser = new JsonParser();
    private static final Gson gson;

    private static final Gson defaultGson = new Gson();

    static {
        gsonBuilder.registerTypeAdapter(Plot.class, Plot.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(Plot.class, Plot.SERIALIZER);
        gsonBuilder.registerTypeAdapter(PropertyPlot.class, Plot.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(PropertyPlot.class, Plot.SERIALIZER);
        gsonBuilder.registerTypeAdapter(StreetPlot.class, Plot.DESERIALIZER);
        gsonBuilder.registerTypeAdapter(StreetPlot.class, Plot.SERIALIZER);
        gson = gsonBuilder.create();
    }

    public static JsonElement parseJson(String y) {
        return parser.parse(y);
    }

    public static JsonElement getJsonElement(Object o) {
        return gson.toJsonTree(o);
    }

    public static <T> T getObject(JsonElement element, Class<T> clazz) {
        return gson.fromJson(element, clazz);
    }

    public static JsonElement getDefaultJsonElement(Object o) {
        return defaultGson.toJsonTree(o);
    }

    public static <T> T getDefaultObject(JsonElement element, Class<T> clazz) {
        return defaultGson.fromJson(element, clazz);
    }

}
