package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.connection.event.ClientNickChangeEvent;

@PacketLinkType(PacketType.NICK_EDIT)
public class PacketLinkMonoNickEdit extends PacketLinkMono {
    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        for(ClientConnection i : connection.getPool().getImmutableConnections()) {
            if(i.getNick().equals(content.getAsString())) {
                return;
            }
        }

        connection.setNick(content.getAsString());
        connection.send(PacketType.NICK_APPROVED, new JsonPrimitive(connection.getNick()));
        ServerRouter.getEventBus().post(new ClientNickChangeEvent(connection.getId(), connection.getNick()));
    }
}
