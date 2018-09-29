package ph.adamw.calloji.packet.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerUpdate {
    private final Player player;
    private final long id;
    private final String nick;
}
