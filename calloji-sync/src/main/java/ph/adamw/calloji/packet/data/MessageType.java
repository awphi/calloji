package ph.adamw.calloji.packet.data;

import javafx.scene.paint.Color;
import lombok.Getter;

public enum MessageType {
    // Used for chat messages from fellow players
    CHAT(Color.BLACK),

    // Used for general game messages i.e. paying rent, passing go, pulling a chance card etc.
    SYSTEM(Color.GREY),

    // Used for severe game messages i.e. verge of bankruptcy, being jailed etc. caution: WARNING will force chat focus
    WARNING(Color.RED),

    // Used for game-independent messages i.e. players leaving/joining, game restarting etc. caution: ADMIN will force chat focus
    ADMIN(Color.DARKBLUE);

    @Getter
    private final Color color;

    MessageType(Color color) {
        this.color = color;
    }
}
