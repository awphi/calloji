package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.monopoly.MonoGame;
import ph.adamw.calloji.server.monopoly.MonoPlayer;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.AUCTION_REQUEST)
@Log4j2
public class PacketLinkMonoAuctionRequest extends PacketLinkMonoTurnOnly {
    @Override
    public void handle(PacketType type, JsonElement content, MonoPlayer player) {
        final PropertyPlot plot = JsonUtils.getObject(content, PropertyPlot.class);

        final MonoGame game = ServerRouter.getGame();
        final PropertyPlot stoodOn = (PropertyPlot) game.getMonoBoard().getIndexedPlot(game.getCurrentTurnPlayer().getPlayer().boardPosition);

        final boolean plotOwned = player.getPlayer().getOwnedPlots(game.getMonoBoard().getBoard()).contains(stoodOn);
        final boolean plotLandedOn = stoodOn.equals(plot) && stoodOn.getOwner() == null;

        if(plotLandedOn) {
            game.auction(stoodOn, null);
        } else if(plotOwned && !(plot.isMortgaged() || plot.isBuiltOn())) {
            game.auction(stoodOn, player);
        }
    }
}
