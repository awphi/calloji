package ph.adamw.calloji.packet.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatMessage {
    private final MessageType type;
    private final String message;
    private final String sender;
}
