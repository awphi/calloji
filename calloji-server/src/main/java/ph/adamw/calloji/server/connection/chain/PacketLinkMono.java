package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.ClientConnection;

@Slf4j
@AllArgsConstructor
public abstract class PacketLinkMono extends PacketLinkBase {
    private final ClientConnection connection;

    public void handle(PacketType type, JsonElement content) {
        handle(type, content, connection);
    }

    public abstract void handle(PacketType type, JsonElement content, ClientConnection connection);
}
