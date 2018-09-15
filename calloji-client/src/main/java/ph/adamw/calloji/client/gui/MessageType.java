package ph.adamw.calloji.client.gui;

import javafx.scene.paint.Color;
import lombok.Getter;

public enum MessageType {
    CHAT(Color.BLACK),
    SYSTEM(Color.GREY);

    @Getter
    private final Color color;

    MessageType(Color color) {
        this.color = color;
    }
}
