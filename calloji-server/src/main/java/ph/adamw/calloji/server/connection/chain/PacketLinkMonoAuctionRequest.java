package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.monopoly.MonoGame;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.AUCTION_REQUEST)
@Slf4j
public class PacketLinkMonoAuctionRequest extends PacketLinkMono {
    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        final MonoGame game = ServerRouter.getGame();
        final PropertyPlot plot = JsonUtils.getObject(content, PropertyPlot.class);
        final PropertyPlot stoodOn = (PropertyPlot) game.getMonoBoard().getIndexedPlot(game.getCurrentTurnPlayer().getPlayer().boardPosition);

        final boolean plotOwned = game.getCurrentTurnPlayer().getPlayer().getOwnedPlots(game.getMonoBoard().getBoard()).contains(stoodOn);
        final boolean plotLandedOn = game.getCurrentTurnPlayer().equals(game.getMonoPlayer(connection.getId())) && stoodOn.equals(plot) && stoodOn.getOwner() == null;

        if(plotLandedOn) {
            game.auction(stoodOn, null);
        } else if(plotOwned && !plot.isClean()) {
            game.auction(stoodOn, game.getMonoPlayer(connection.getId()));
        }
    }
}
