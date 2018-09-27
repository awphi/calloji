package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.data.ChatMessage;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.JsonUtils;

public class PacketLinkChat extends PacketLink {
    public PacketLinkChat() {
        super(PacketType.CHAT);
    }

    @Override
    public void handle(PacketType type, JsonElement content) {
        final ChatMessage message = JsonUtils.getObject(content, ChatMessage.class);
        Client.printMessage(MessageType.CHAT, message.getSender() + ": " + message.getMessage());
    }
}
