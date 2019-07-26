package ph.adamw.calloji.packet.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BidUpdate {
    private final String winnerNick;
    private final int amount;
}
