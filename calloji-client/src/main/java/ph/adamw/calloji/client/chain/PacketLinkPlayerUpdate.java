package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.data.PlayerUpdate;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.JsonUtils;

@Slf4j
@PacketLinkType(PacketType.PLAYER_UPDATE)
public class PacketLinkPlayerUpdate extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final PlayerUpdate update = JsonUtils.getObject(content, PlayerUpdate.class);
        Client.getCache().cachePlayer(update);
        Platform.runLater(() ->  Client.getGui().loadPlayer(update));
    }
}
