package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.monopoly.MonoGame;
import ph.adamw.calloji.server.monopoly.MonoPlayer;

/**
 * Simple wrapper class for server-side packet handlers that will only handle the packet if it was received from the
 * player whose turn it is.
 */
public abstract class PacketLinkMonoTurnOnly extends PacketLinkMono {
    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        final MonoGame game = ServerRouter.getGame();
        final MonoPlayer player = game.getMonoPlayer(connection.getId());

        if(player == game.getCurrentTurnPlayer()) {
            handle(type, content, game.getCurrentTurnPlayer());
        }
    }

    public abstract void handle(PacketType type, JsonElement content, MonoPlayer player);
}
