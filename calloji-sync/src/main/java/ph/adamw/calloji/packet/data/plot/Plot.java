package ph.adamw.calloji.packet.data.plot;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.util.JsonUtils;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Plot implements Serializable {
    public final transient static JsonSerializer<Plot> SERIALIZER = (plot, type, jsonSerializationContext) -> {
        final JsonObject object = new JsonObject();
        object.addProperty("class", plot.getClass().getSimpleName());
        final JsonElement payload = JsonUtils.getDefaultJsonElement(plot);
        object.add("plot", payload);
        return object;
    };

    public final transient static JsonDeserializer<Plot> DESERIALIZER = (jsonElement, type, jsonDeserializationContext) -> {
        final JsonObject object = (JsonObject) jsonElement;

        switch(object.get("class").getAsString()) {
            case "Plot": return JsonUtils.getDefaultObject(object.get("plot"), Plot.class);
            case "PropertyPlot": return JsonUtils.getDefaultObject(object.get("plot"), PropertyPlot.class);
            case "StreetPlot": return JsonUtils.getDefaultObject(object.get("plot"), StreetPlot.class);
        }

        return null;
    };

    private final String name;
    private final PlotType type;

    public static Plot getCommonInstance(PlotType type) {
        switch (type) {
            case GO: return new Plot("Go", type);
            case CHANCE: return new Plot("Chance", type);
            case COMMUNITY_CHEST: return new Plot("Community Chest", type);
        }

        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Plot) {
            final Plot p = (Plot) obj;
            return p.name.equals(name) && p.type == type;
        }

        return super.equals(obj);
    }
}
