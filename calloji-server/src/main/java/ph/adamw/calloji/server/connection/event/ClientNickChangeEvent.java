package ph.adamw.calloji.server.connection.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ClientNickChangeEvent {
    private long id;
    
    private String newNick;
}
