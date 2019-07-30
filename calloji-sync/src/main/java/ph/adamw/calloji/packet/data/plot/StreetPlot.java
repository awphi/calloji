package ph.adamw.calloji.packet.data.plot;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.calloji.util.GameConstants;

import java.io.Serializable;

public class StreetPlot extends PropertyPlot {
    @Getter
    @Setter
    private int houses = 0;

    @Getter
    private final int buildCost;

    public StreetPlot(String name, PlotType type, int value, int buildCost) {
        super(name, type, value);

        this.buildCost = buildCost;
    }

    @Override
    public boolean isBuiltOn() {
        return getHouses() > 0;
    }

    public int getRent() {
        final int y = (getValue() / 10) - 4;
        return y * GameConstants.HOUSE_MULTIPLIERS[houses];
    }
}
