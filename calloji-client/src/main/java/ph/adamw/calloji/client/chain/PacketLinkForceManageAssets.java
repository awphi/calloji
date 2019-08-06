package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;

@PacketLinkType(PacketType.FORCE_MANAGE_ASSETS)
public class PacketLinkForceManageAssets extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        //TODO
    }
}
