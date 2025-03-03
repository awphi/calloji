package ph.adamw.calloji.packet.data.plot;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
public class PropertyPlot extends Plot {
    @Setter
    @Nullable
    private Long owner;

    @Setter
    private boolean isMortgaged = false;

    private final int value;

    public PropertyPlot(String name, PlotType type, int value) {
        super(name, type);

        this.value = value;
    }

    public boolean isBuiltOn() {
        return false;
    }

    public int getUnmortgageCost() {
        return (int) ((getValue() / 2) + (getValue() * 0.1));
    }
}