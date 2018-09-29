package ph.adamw.calloji.packet.data.plot;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.calloji.packet.data.Player;

import java.io.Serializable;

@Getter
public class PropertyPlot extends Plot implements Serializable {
    @Setter
    private Player owner = null;

    @Setter
    private boolean isMortgaged = false;

    private final int value;

    public PropertyPlot(String name, PlotType type, int value) {
        super(name, type);

        this.value = value;
    }
}