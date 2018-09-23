package ph.adamw.calloji.data.plot;

import lombok.Getter;

import java.io.Serializable;

public class Plot implements Serializable {
    @Getter
    private final String name;

    @Getter
    private final PlotType type;

    public Plot(String name, PlotType type) {
        this.name = name;
        this.type = type;
    }

    public static Plot getCommonInstance(PlotType type) {
        switch (type) {
            case GO: return new Plot("Go", type);
            case CHANCE: return new Plot("Chance", type);
            case COMMUNITY_CHEST: return new Plot("Community Chest", PlotType.COMMUNITY_CHEST);
        }

        return null;
    }
}
