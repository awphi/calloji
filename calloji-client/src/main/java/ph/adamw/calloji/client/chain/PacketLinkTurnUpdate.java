package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;

@PacketLinkType(PacketType.TURN_UPDATE)
public class PacketLinkTurnUpdate extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final boolean isInputAllowed = content.getAsBoolean();

        Platform.runLater(() -> Client.getGui().setOurTurn(isInputAllowed));
    }
}
