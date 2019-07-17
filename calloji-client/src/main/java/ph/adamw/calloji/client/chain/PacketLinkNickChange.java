package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;

@PacketLinkType(PacketType.NICK_APPROVED)
public class PacketLinkNickChange extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final String nick = content.getAsString();

        Client.getGui().displayChatMessage(MessageType.SYSTEM, "Nickname successfully changed to " + nick + ".");

        Platform.runLater(() -> {
            Client.getGui().getNicknameMenu().setText(nick);
            Client.getGui().getNicknameMenu().show();
            Client.getGui().getNicknameMenu().hide();
        });
    }
}
