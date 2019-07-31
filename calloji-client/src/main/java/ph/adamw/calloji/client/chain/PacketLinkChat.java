package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.data.MessageType;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.data.ChatMessage;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.CHAT_MESSAGE)
public class PacketLinkChat extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final ChatMessage message = JsonUtils.getObject(content, ChatMessage.class);
        final String txt = message.getType().equals(MessageType.CHAT) ? message.getSender() + ": " + message.getMessage() : message.getMessage();
        Client.getGui().displayChatMessage(message.getType(), txt);
    }
}
