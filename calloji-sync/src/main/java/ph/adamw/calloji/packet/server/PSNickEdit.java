package ph.adamw.calloji.packet.server;

import lombok.AllArgsConstructor;
import ph.adamw.calloji.packet.client.IClient;
import ph.adamw.calloji.packet.client.PC;

@AllArgsConstructor
public class PSNickEdit extends PS {
    private final String nick;

    @Override
    public void handle(IClientConnection server) {
        server.onNickEditRequest(nick);
    }

    @AllArgsConstructor
    public static class Ack extends PC {
        public final String nick;

        @Override
        public void handle(IClient client) {
            client.onNickChanged(nick);
        }
    }
}
