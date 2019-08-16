package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.monopoly.MonoPlayer;

@PacketLinkType(PacketType.END_TURN_REQUEST)
public class PacketLinkMonoEndTurn extends PacketLinkMonoTurnOnly {
    @Override
    public void handle(PacketType type, JsonElement content, MonoPlayer player) {
        ServerRouter.getGame().endCurrentTurn();
    }
}
