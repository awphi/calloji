package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.monopoly.MonoGame;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.AUCTION_BID)
public class PacketLinkMonoBidRequest extends PacketLinkMono {
    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        final int amount = JsonUtils.getObject(content, Integer.class);
        final MonoGame game = ServerRouter.getGame();

        game.getActiveAuction().attemptBid(game.getMonoPlayer(connection.getId()), amount);
    }
}
