package ph.adamw.calloji.packet.server;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PSNickEdit extends PS {
    private final String nick;

    @Override
    public void handle(IClientConnection server) {
        server.onNickEditRequest(nick);
    }
}
