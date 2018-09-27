package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.data.ConnectionUpdate;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.server.PSHeartbeat;
import ph.adamw.calloji.util.JsonUtils;

public class PacketLinkConnect extends PacketLink {
    public PacketLinkConnect() {
        super(PacketType.CLIENT_CONNECTION_UPDATE);
    }

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
                    Client.getRouter().send(new PSHeartbeat());

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
