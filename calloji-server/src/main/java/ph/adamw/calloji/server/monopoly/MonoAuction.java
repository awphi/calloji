package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.BidUpdate;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.server.ServerRouter;

@Getter
public class MonoAuction {
    private final PropertyPlot property;
    private MonoPlayer winner = null;
    private int winnerBid;

    public MonoAuction(PropertyPlot property) {
        this.property = property;
    }

    public void attemptBid(MonoPlayer player, int amount) {
        if(winner == player || amount <= winnerBid || player.getPlayer().getBalance() < amount) {
            return;
        }

        winnerBid = amount;
        winner = player;
        ServerRouter.getGame().sendToAll(PacketType.TOP_BIDDER_UPDATE, new BidUpdate(winner.getConnectionNick(), winnerBid));
    }
}
