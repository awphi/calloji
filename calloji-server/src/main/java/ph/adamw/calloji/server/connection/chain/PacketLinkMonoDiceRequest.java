package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.monopoly.MonoPlayer;

@PacketLinkType(PacketType.ROLL_DICE_REQ)
public class PacketLinkMonoDiceRequest extends PacketLinkMonoTurnOnly {
    @Override
    public void handle(PacketType type, JsonElement content, MonoPlayer player) {
        ServerRouter.getGame().rollDice(player);
    }
}
