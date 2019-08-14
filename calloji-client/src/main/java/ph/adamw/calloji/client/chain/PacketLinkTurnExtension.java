package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.TURN_EXTENSION)
public class PacketLinkTurnExtension extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final int secs = JsonUtils.getObject(content, Integer.class);
        Client.getGui().extendTurnTimer(secs);
    }
}
