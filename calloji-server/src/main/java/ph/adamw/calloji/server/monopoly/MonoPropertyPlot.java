package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.Player;
import ph.adamw.calloji.packet.data.plot.PlotType;
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

    protected boolean sellIfUnowned(MonoPlayer player) {
        if(plot.getOwner() == null) {
            if (plot.getValue() > player.getPlayer().getBalance()) {
                game.auction(plot, null);
            } else {
                player.send(PacketType.PLOT_LANDED_ON, plot);
            }

            return true;
        }

        return false;
    }

    public void landedOnBy(MonoPlayer player, int spacesToMove) {
        if(sellIfUnowned(player)) {
            return;
        }

        final MonoPlayer owner = game.getMonoPlayer(plot.getOwner());

        if(plot.getType() == PlotType.UTILITY) {
            final int owned = owner.getPlayer().getOwnedType(PlotType.UTILITY, game.getMonoBoard().getBoard());
            owner.addMoney(player.tryRemoveMoney(owned * 5 + (owned - 2) * spacesToMove));
        } else if(plot.getType() == PlotType.STATION) {
            final int owned = owner.getPlayer().getOwnedType(PlotType.STATION, game.getMonoBoard().getBoard());
            owner.addMoney(player.tryRemoveMoney(25 * (int) Math.pow(2, owned - 1)));
        }
    }
}
