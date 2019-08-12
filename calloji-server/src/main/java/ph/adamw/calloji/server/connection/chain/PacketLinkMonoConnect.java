package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.ConnectionUpdate;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.CLIENT_CONNECTION_UPDATE)
@Log4j2
public class PacketLinkMonoConnect extends PacketLinkMono {
    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        final ConnectionUpdate connUpdate = JsonUtils.getObject(content, ConnectionUpdate.class);

        if(!connUpdate.isDisconnect()) {
            return;
        }

        log.debug("Disconnecting client peacefully: " + connection.getId());

        final Thread killThread = connection.getKillThread();
        if(killThread != null) {
            killThread.interrupt();
        }

        connection.disconnect();
    }
}
