package ph.adamw.calloji.packet.data.plot;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class StreetPlot extends PropertyPlot implements Serializable {
    private static final int[] HOUSE_MULTIPLIERS = new int[] {1, 5, 15, 35, 42, 50};

    @Getter
    @Setter
    private int houses = 0;

    @Getter
    private final int buildCost;

    public StreetPlot(String name, PlotType type, int value, int buildCost) {
        super(name, type, value);

        this.buildCost = buildCost;
    }

    public int getRent() {
        final int y = (getValue() / 10) - 4;
        return y * HOUSE_MULTIPLIERS[houses];
    }
}
