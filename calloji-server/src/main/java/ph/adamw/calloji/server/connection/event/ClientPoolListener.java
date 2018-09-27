package ph.adamw.calloji.server.connection.event;

import com.google.common.eventbus.Subscribe;
import ph.adamw.calloji.server.ServerRouter;

public abstract class  ClientPoolListener {
    public ClientPoolListener() {
        ServerRouter.getEventBus().register(this);
    }

    @Subscribe
    public abstract void onClientConnect(ClientConnectedEvent e);

    @Subscribe
    public abstract void onClientDisconnect(ClientDisconnectedEvent e);

    @Subscribe
    public abstract void onClientNickChanged(ClientNickChangeEvent e);
}
