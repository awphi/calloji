package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.CardUpdate;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.CARD_DRAWN)
public class PacketLinkCardDrawn extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final CardUpdate update = JsonUtils.getObject(content, CardUpdate.class);
        //TODO card gui
    }
}
