package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.ClientConnection;

@Log4j2
public abstract class PacketLinkMono extends PacketLinkBase {
    @Setter(AccessLevel.PRIVATE)
    private ClientConnection connection;

    public void handle(PacketType type, JsonElement content) {
        handle(type, content, connection);
    }

    public abstract void handle(PacketType type, JsonElement content, ClientConnection connection);

    public PacketLinkMono setChainConnection(ClientConnection conn) {
        PacketLinkMono plink = this;

        do {
            plink.setConnection(conn);
            plink = (PacketLinkMono) plink.getSuccessor();
        } while (plink != null);

        return this;
    }
}
