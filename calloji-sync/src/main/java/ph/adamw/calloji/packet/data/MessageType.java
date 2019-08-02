package ph.adamw.calloji.packet.data;

import javafx.scene.paint.Color;
import lombok.Getter;

public enum MessageType {
    CHAT(Color.BLACK),
    SYSTEM(Color.GREY),
    WARNING(Color.RED),
    ADMIN(Color.DARKBLUE);

    @Getter
    private final Color color;

    MessageType(Color color) {
        this.color = color;
    }
}
