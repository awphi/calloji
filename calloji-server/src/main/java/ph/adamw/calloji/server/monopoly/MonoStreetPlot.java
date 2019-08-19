package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import ph.adamw.calloji.packet.data.MessageType;
import ph.adamw.calloji.packet.data.plot.StreetPlot;
import ph.adamw.calloji.util.GameConstants;

public class MonoStreetPlot extends MonoPropertyPlot {
    @Getter
    private StreetPlot plot;

    MonoStreetPlot(MonoGame game, StreetPlot plot) {
        super(game, plot);

        this.plot = plot;
    }

    public void buildHouse() {
        if(plot.getHouses() + 1 <= GameConstants.MAX_HOUSES) {
            plot.setHouses(plot.getHouses() + 1);
            game.updateBoardOnAllClients();
            game.getMonoPlayer(plot.getOwner()).tryRemoveMoney(plot.getBuildCost());
        }
    }

    public void sellHouse() {
        if(plot.getHouses() - 1 >= 0) {
            plot.setHouses(plot.getHouses() - 1);
            game.updateBoardOnAllClients();
            game.getMonoPlayer(plot.getOwner()).addMoney(plot.getBuildCost() / 2);
        }
    }

    @Override
    public void landedOnBy(MonoPlayer player, int spacesToMove) {
        if(sellIfUnowned(player) || plot.getOwner().equals(player.getConnectionId()) && !plot.isMortgaged()) {
            return;
        }

        final StreetPlot st = plot;
        final MonoPlayer owner = game.getMonoPlayer(plot.getOwner());

        int amount = player.tryRemoveMoney(st.getRent());
        owner.addMoney(amount);

        if(amount > 0) {
            owner.sendMessage(MessageType.SYSTEM, "You received a rent payment of £" + amount + ".00 from " + player.getConnectionNick() + " for " + plot.getName() + ".");
            player.sendMessage(MessageType.SYSTEM, "You paid rent of £" + amount + ".00 to " + owner.getConnectionNick() + " for " + plot.getName() + ".");
        }
    }
}
