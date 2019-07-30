package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.StreetPlot;
import ph.adamw.calloji.util.GameConstants;

public class MonoStreetPlot extends MonoPropertyPlot {
    @Getter
    private StreetPlot plot;

    MonoStreetPlot(MonoGame game, StreetPlot plot) {
        super(game, plot);

        this.plot = plot;
    }

    public void buildHouses(int i) {
        if(plot.getHouses() + i <= GameConstants.MAX_HOUSES) {
            plot.setHouses(plot.getHouses() + i);
            game.updateBoardOnAllClients();
            game.getMonoPlayer(plot.getOwner()).tryRemoveMoney(plot.getBuildCost() * i);
        }
    }

    public void sellHouses(int amount) {
        if(plot.getHouses() - amount >= 0) {
            final int toRemove = Math.min(amount, plot.getHouses());
            plot.setHouses(plot.getHouses() - toRemove);
            game.updateBoardOnAllClients();
            game.getMonoPlayer(plot.getOwner()).addMoney(toRemove * (plot.getBuildCost() / 2));
        }
    }

    @Override
    public void landedOnBy(MonoPlayer player, int spacesToMove) {
        if(sellIfUnowned(player)) {
            return;
        }

        if(!plot.getOwner().equals(player.getConnectionId()) && !plot.isMortgaged()) {
            final StreetPlot st = plot;
            int rem = player.tryRemoveMoney(st.getRent());

            if(player.getPlayer().isBankrupt()) {
                game.getMonoPlayer(plot.getOwner()).addMoney(rem);
            }
        }
    }
}
