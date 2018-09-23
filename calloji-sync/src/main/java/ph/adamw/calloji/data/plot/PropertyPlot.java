package ph.adamw.calloji.data.plot;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.calloji.data.Player;

import java.io.Serializable;

public class PropertyPlot extends Plot implements Serializable {
    @Getter
    @Setter
    private Player owner = null;

    @Getter
    @Setter
    private boolean isMortgaged = false;

    @Getter
    private final int value;

    public PropertyPlot(String name, PlotType type, int value) {
        super(name, type);

        this.value = value;
    }
}