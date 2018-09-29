package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.ClientConnection;

@PacketLinkType(PacketType.HEARTBEAT)
@Slf4j
public class PacketLinkMonoHeartbeat extends PacketLinkMono {
    public PacketLinkMonoHeartbeat(ClientConnection connection) {
        super(connection);
    }

    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        connection.restartKillThread();
    }
}
