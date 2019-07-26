package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.ClientConnection;

public class PacketLinkMonoBidReceived extends PacketLinkMono {
    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        //TODO on bid received check if amount is high enough, if user can afford, if auction over etc. etc.
    }
}
