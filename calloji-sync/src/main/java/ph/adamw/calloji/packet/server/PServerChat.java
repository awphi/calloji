package ph.adamw.calloji.packet.server;

import lombok.AllArgsConstructor;
import ph.adamw.calloji.packet.client.IClient;
import ph.adamw.calloji.packet.client.PClient;

@AllArgsConstructor
public class PServerChat extends PServer {
    private final String message;

    @Override
    public void handle(IClientConnection server) {
        server.onChatReceived(message);
    }

    @AllArgsConstructor
    public static class Ack extends PClient {
        private final String from;
        private final String message;

        @Override
        public void handle(IClient client) {
            client.onChatReceived(from, message);
        }
    }
}
