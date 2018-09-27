package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.data.PlayerUpdate;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.JsonUtils;

@Slf4j
public class PacketLinkPlayerUpdate extends PacketLink {
    public PacketLinkPlayerUpdate() {
        super(PacketType.PLAYER_UPDATE);
    }

    @Override
    public void handle(PacketType type, JsonElement content) {
        final PlayerUpdate update = JsonUtils.getObject(content, PlayerUpdate.class);

        if(update.getId() == Client.getRouter().getPid()) {
            log.info("Received new player update of this player!");
            Platform.runLater(() ->  Client.getGui().loadThisPlayer(update));
        } else {
            log.info("Received new player update for opponent: " + update.getId());
            Platform.runLater(() -> Client.getGui().loadGenericPlayer(update));
        }
    }
}
