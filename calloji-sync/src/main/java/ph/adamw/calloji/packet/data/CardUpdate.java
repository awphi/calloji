package ph.adamw.calloji.packet.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CardUpdate {
    public String deck;
    public String text;
}
