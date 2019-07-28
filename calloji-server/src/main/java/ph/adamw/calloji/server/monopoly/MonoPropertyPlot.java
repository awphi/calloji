package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

@Slf4j
public class MonoPropertyPlot {
    @Getter
    private final PropertyPlot plot;

    final MonoGame game;

    MonoPropertyPlot(MonoGame game, PropertyPlot plot) {
        this.plot = plot;
        this.game = game;
    }


    public void mortgage() {
        if(plot.getOwner() != null && !plot.isMortgaged()) {
            plot.setMortgaged(true);
            game.updateBoardOnAllClients();
            game.getMonoPlayer(plot.getOwner()).addMoney(plot.getValue() / 2);
        }
    }

    public void unmortgage() {
        log.debug(plot.isMortgaged() + "");
        if(plot.getOwner() != null && plot.isMortgaged()) {
            plot.setMortgaged(false);
            game.updateBoardOnAllClients();
            game.getMonoPlayer(plot.getOwner()).tryRemoveMoney(plot.getUnmortgageCost());
        }
    }
}
