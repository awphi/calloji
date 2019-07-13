package ph.adamw.calloji.server.connection.event;

import com.google.common.eventbus.Subscribe;

public interface ClientPoolListener {
    @Subscribe
    void onClientConnect(ClientConnectedEvent e);

    @Subscribe
    void onClientDisconnect(ClientDisconnectedEvent e);

    @Subscribe
    void onClientNickChanged(ClientNickChangeEvent e);
}
