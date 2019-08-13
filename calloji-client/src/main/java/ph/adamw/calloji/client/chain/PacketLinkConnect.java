package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.data.MessageType;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.data.ConnectionUpdate;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.GameConstants;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.CLIENT_CONNECTION_UPDATE)
public class PacketLinkConnect extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final ConnectionUpdate conn = JsonUtils.getObject(content, ConnectionUpdate.class);

        if(conn.isDisconnect()) {
            Client.getGui().displayChatMessage(MessageType.SYSTEM, Client.getCache().getCachedPlayer(conn.getId()).getNick() + " has disconnected.");
            Platform.runLater(() -> Client.getGui().removeOtherPlayer(conn.getId()));
        } else {
            Client.getRouter().setPid(conn.getId());
            Client.getGui().displayChatMessage(MessageType.SYSTEM, "Connected to server!");
        }
    }
}
