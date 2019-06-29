package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

public class MonoStreetPlot extends MonoPropertyPlot {
    @Getter
    private StreetPlot plot;

    MonoStreetPlot(MonoGame game, StreetPlot plot) {
        super(game, plot);

        this.plot = plot;
    }


    public void buildHouses(int i) {
        plot.setHouses(plot.getHouses() + i);
    }

    public void sellHouses(int amount) {
        final int toRemove = Math.min(amount, plot.getHouses());
        plot.setHouses(plot.getHouses() - toRemove);
        game.getMonoPlayer(plot.getOwner()).addMoney(toRemove * (plot.getBuildCost() / 2));
    }
}
