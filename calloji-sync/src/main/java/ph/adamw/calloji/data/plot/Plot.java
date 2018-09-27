package ph.adamw.calloji.data.plot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class Plot implements Serializable {
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
}
