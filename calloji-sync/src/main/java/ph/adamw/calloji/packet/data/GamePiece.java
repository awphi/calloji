package ph.adamw.calloji.packet.data;

import lombok.Getter;

public enum GamePiece {
    BATTLESHIP("battleship"),
    TOP_HAT("top_hat"),
    SCOTTY_DOG("scotty_dog"),
    IRON("iron"),
    RACECAR("racecar"),
    BOOT("init"),
    WHEELBARROW("wheelbarrow");

    @Getter
    private final String imageRef;

    private static int count = 0;

    private static final GamePiece[] VALUES = values();

    GamePiece(String imageRef) {
        this.imageRef = imageRef;
    }

    public static GamePiece next() {
        final GamePiece ret = VALUES[count];
        count = (count + 1) % VALUES.length;
        return ret;
    }
}
