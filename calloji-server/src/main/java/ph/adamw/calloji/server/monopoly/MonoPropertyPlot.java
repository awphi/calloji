package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

public class MonoPropertyPlot {
    @Getter
    private final PropertyPlot plot;

    final MonoGame game;

    MonoPropertyPlot(MonoGame game, PropertyPlot plot) {
        this.plot = plot;
        this.game = game;
    }


    public void mortgage() {
        if(plot.getOwner() != null) {
            plot.setMortgaged(true);
            game.getMonoPlayer(plot.getOwner()).addMoney(plot.getValue() / 2);
        }
    }

    public void unmortgage() {
        if(plot.getOwner() != null) {
            plot.setMortgaged(false);
            game.getMonoPlayer(plot.getOwner()).tryRemoveMoney((int) ((plot.getValue() / 2) + (plot.getValue() * 0.1)));
        }
    }
}
