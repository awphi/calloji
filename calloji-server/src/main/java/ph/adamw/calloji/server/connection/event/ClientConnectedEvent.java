package ph.adamw.calloji.server.connection.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ph.adamw.calloji.server.connection.ClientPool;

@AllArgsConstructor
@Getter
public class ClientConnectedEvent {
    private long id;
    private ClientPool pool;
}