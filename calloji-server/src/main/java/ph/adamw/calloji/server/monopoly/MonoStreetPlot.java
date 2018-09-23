package ph.adamw.calloji.server.monopoly;

import ph.adamw.calloji.data.plot.PlotType;
import ph.adamw.calloji.data.plot.StreetPlot;

public class MonoStreetPlot extends StreetPlot {
    public MonoStreetPlot(String name, PlotType type, int value, int buildCost) {
        super(name, type, value, buildCost);
    }

    public void buildHouse() {
        setHouses(getHouses() + 1);
    }

    public void sellHouses(int amount) {
        final int toRemove = Math.min(amount, getHouses());
        setHouses(getHouses() - toRemove);
        ((MonoPlayer) getOwner()).addMoney(toRemove * (getBuildCost() / 2));
    }
}
