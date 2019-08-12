package ph.adamw.calloji.server.connection;

import com.google.gson.JsonElement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.PacketDispatcher;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.chain.*;
import ph.adamw.calloji.util.GameConstants;
import ph.adamw.calloji.util.PacketLinkUtils;

import java.io.*;
import java.net.Socket;

@Log4j2
public class ClientConnection extends PacketDispatcher {
    @Getter
    private final Socket socket;

    @Getter(AccessLevel.PROTECTED)
    private final OutputStream outputStream;

    @Getter(AccessLevel.PROTECTED)
    private final InputStream inputStream;

    @Getter
    private Thread killThread;
    private boolean isDead = false;

    @Getter
    private final ClientPool pool;

    @Getter
    private final long id;

    @Setter
    @Getter
    private String nick = "Calloji User";

    private final PacketLinkMono linkChain = ((PacketLinkMono) PacketLinkUtils.buildChain()).setChainConnection(this);

    ClientConnection(long id, ClientPool pool, Socket socket) throws IOException {
        this.socket = socket;
        this.pool = pool;
        this.id = id;

        outputStream = socket.getOutputStream();
        outputStream.flush();

        inputStream = socket.getInputStream();

        startReceiving();
    }

    public void restartKillThread() {
        if(killThread != null) {
            killThread.interrupt();
        }

        // We need to receive a heart beat every x milliseconds to keep the heart beating, if it's not
        killThread = new Thread(() -> {
            try {
                Thread.sleep(GameConstants.TIMEOUT_TIME * 1000);
            } catch (InterruptedException ignored) {
                return;
            }

            log.info("Failed to receive a heartbeat from: " + id + ", forcefully closing their connection now!");
            disconnect();
        }, "Kill" + id);

        if(!isDead) {
            killThread.start();
        }
    }

    public void disconnect() {
        pool.removeConn(id);
        isDead = true;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleLink(PacketType packetType, JsonElement content) {
        linkChain.handleLink(packetType, content);
    }

    @Override
    protected boolean isConnected() {
        return !isDead && !socket.isClosed() && outputStream != null && socket.isConnected();
    }
}
