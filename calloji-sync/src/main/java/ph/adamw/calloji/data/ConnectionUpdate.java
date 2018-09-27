package ph.adamw.calloji.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConnectionUpdate {
    private final boolean isDisconnect;
    private final long id;
}
