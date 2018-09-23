package ph.adamw.calloji.server.monopoly;

import ph.adamw.calloji.data.plot.PlotType;
import ph.adamw.calloji.data.plot.PropertyPlot;

public class MonoPropertyPlot extends PropertyPlot {
    public MonoPropertyPlot(String name, PlotType type, int value) {
        super(name, type, value);
    }


    public void mortgage() {
        if(getOwner() != null) {
            setMortgaged(true);
            ((MonoPlayer) getOwner()).addMoney(getValue() / 2);
        }
    }

    public void unmortgage() {
        if(getOwner() != null) {
            setMortgaged(false);
            ((MonoPlayer) getOwner()).tryRemoveMoney((int) ((getValue() / 2) + (getValue() * 0.1)));
        }
    }
}
