package ph.adamw.calloji.packet.data.plot;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.calloji.packet.data.Player;

import java.io.Serializable;

@Getter
public class PropertyPlot extends Plot {
    @Setter
    private Long owner;

    @Setter
    private boolean isMortgaged = false;

    private final int value;

    public PropertyPlot(String name, PlotType type, int value) {
        super(name, type);

        this.value = value;
    }

    public boolean isBuiltOnOrMortgaged() {
        return isMortgaged() || isBuiltOn();
    }

    public boolean isBuiltOn() {
        if(this instanceof StreetPlot) {
            return ((StreetPlot) this).getHouses() > 0;
        }

        return false;
    }

    public int getUnmortgageCost() {
        return (int) ((getValue() / 2) + (getValue() * 0.1));
    }
}