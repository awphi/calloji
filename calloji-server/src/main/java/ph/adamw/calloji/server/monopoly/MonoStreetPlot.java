package ph.adamw.calloji.server.monopoly;

import ph.adamw.calloji.data.plot.StreetPlot;

public class MonoStreetPlot extends MonoPropertyPlot {
    private StreetPlot plot;

    MonoStreetPlot(MonoGame game, StreetPlot plot) {
        super(game, plot);

        this.plot = plot;
    }


    public void buildHouse() {
        plot.setHouses(plot.getHouses() + 1);
    }

    public void sellHouses(int amount) {
        final int toRemove = Math.min(amount, plot.getHouses());
        plot.setHouses(plot.getHouses() - toRemove);
        game.getMonoPlayer(plot.getOwner()).addMoney(toRemove * (plot.getBuildCost() / 2));
    }
}
