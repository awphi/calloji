package ph.adamw.calloji.packet.server;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PSChat extends PS {
    private final String message;

    @Override
    public void handle(IClientConnection server) {
        server.onChatReceived(message);
    }
}
