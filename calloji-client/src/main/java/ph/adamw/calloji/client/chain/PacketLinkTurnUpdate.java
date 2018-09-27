package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketType;

public class PacketLinkTurnUpdate extends PacketLink {
    public PacketLinkTurnUpdate() {
        super(PacketType.TURN_UPDATE);
    }

    @Override
    public void handle(PacketType type, JsonElement content) {
        final boolean isInputAllowed = content.getAsBoolean();

        Platform.runLater(() -> Client.getGui().setOurTurn(isInputAllowed));
    }
}
