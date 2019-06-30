package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;

@PacketLinkType(PacketType.ROLL_DICE_REQ)
public class PacketLinkMonoDiceRequest extends PacketLinkMono {
    public PacketLinkMonoDiceRequest(ClientConnection connection) {
        super(connection);
    }

    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        if(ServerRouter.getGame().getCurrentTurnPlayer().getConnectionId() == connection.getId()) {
            ServerRouter.getGame().rollDice(connection);
        }
    }
}
