package ph.adamw.calloji.server;

import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.server.IServer;

@Slf4j
public class Server implements IServer {
    @Override
    public void receivedHeartbeat(long client) {
        //log.fine("Received heartbeat from client: " + client);
        ServerRouter.getClientPool().get(client).beatHeart();
    }
}
