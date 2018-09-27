package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.packet.PacketType;

public class PacketLinkNickChange extends PacketLink {
    public PacketLinkNickChange() {
        super(PacketType.NICK_APPROVED);
    }

    @Override
    public void handle(PacketType type, JsonElement content) {
        final String nick = content.getAsString();

        Client.printMessage(MessageType.SYSTEM, "Nickname successfully changed to " + nick + ".");

        Platform.runLater(() -> {
            Client.getGui().getNicknameMenu().setText(nick);
            Client.getGui().getNicknameMenu().show();
            Client.getGui().getNicknameMenu().hide();
        });
    }
}
