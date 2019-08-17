package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.MessageType;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

@Log4j2
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
        if(sellIfUnowned(player) || plot.getOwner().equals(player.getConnectionId()) && !plot.isMortgaged()) {
            return;
        }

        final MonoPlayer owner = game.getMonoPlayer(plot.getOwner());

        int amount = 0;

        if(plot.getType() == PlotType.UTILITY) {
            final int owned = owner.getPlayer().getOwnedType(PlotType.UTILITY, game.getMonoBoard().getBoard());
            amount = owned * 5 + (owned - 2) * spacesToMove;
        } else if(plot.getType() == PlotType.STATION) {
            final int owned = owner.getPlayer().getOwnedType(PlotType.STATION, game.getMonoBoard().getBoard());
            amount = 25 * (int) Math.pow(2, owned - 1);
        }

        owner.addMoney(player.tryRemoveMoney(amount));

        if(amount > 0) {
            owner.sendMessage(MessageType.SYSTEM, "You received a payment of £" + amount + ".00 from " + player.getConnectionNick() + " for use of:  " + plot.getName() + ".");
            player.sendMessage(MessageType.SYSTEM, "You paid £" + amount + ".00 to " + owner.getConnectionNick() + " for use of: " + plot.getName() + ".");
        }
    }
}
