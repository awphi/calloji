package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.data.ConnectionUpdate;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.CLIENT_CONNECTION_UPDATE)
public class PacketLinkConnect extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final ConnectionUpdate conn = JsonUtils.getObject(content, ConnectionUpdate.class);

        if(conn.isDisconnect()) {
            if (conn.getId() == Client.getRouter().getPid()) {
                Client.printMessage(MessageType.SYSTEM, "Disconnected from server.");
                Client.getRouter().forceDisconnect();
            } else {
                Platform.runLater(() -> Client.getGui().removeOtherPlayer(conn.getId()));
            }
        } else {
            Client.getRouter().setPid(conn.getId());
            Client.printMessage(MessageType.SYSTEM, "Connected to server!");

            new Thread(() -> {
                while(Client.getRouter().isConnected()) {
                    Client.getRouter().send(PacketType.HEARTBEAT, new JsonObject());

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
