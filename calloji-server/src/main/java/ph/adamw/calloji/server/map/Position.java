package ph.adamw.calloji.server.map;

import lombok.Getter;

@Getter
public class Position {
    private final int x;
    private final int y;
    private final Realm realm;

    public Position(int x, int y, Realm realm) {
        this.x = x;
        this.y = y;
        this.realm = realm;
    }
}
