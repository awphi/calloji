package ph.adamw.calloji.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatMessage {
    private final String message;
    private final String sender;
}
