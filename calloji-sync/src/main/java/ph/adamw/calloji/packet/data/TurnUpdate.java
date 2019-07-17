package ph.adamw.calloji.packet.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TurnUpdate {
    private final long pid;
    private final int turnTime;
    private final String nick;
}
