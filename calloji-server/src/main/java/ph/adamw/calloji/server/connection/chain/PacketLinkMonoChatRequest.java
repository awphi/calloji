package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.data.ChatMessage;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.MessageType;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.CHAT_MESSAGE)
public class PacketLinkMonoChatRequest extends PacketLinkMono {
    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        final ChatMessage chatMessage = JsonUtils.getObject(content, ChatMessage.class);

        String message = chatMessage.getMessage();

        // Easter egg
        if(message.equals("::bank")) {
            message = "Hey, everyone, I just tried to do something very silly!";
        }

        for(ClientConnection c : connection.getPool().getImmutableConnections()) {
            c.send(PacketType.CHAT_MESSAGE, new ChatMessage(MessageType.CHAT, message, connection.getNick()));
        }
    }
}
