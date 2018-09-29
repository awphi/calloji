package ph.adamw.calloji.server.connection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.PacketDispatcher;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.chain.*;
import ph.adamw.calloji.util.JsonUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

@Slf4j
public class ClientConnection extends PacketDispatcher {
    @Getter
    private final Socket socket;

    @Getter(AccessLevel.PROTECTED)
    private final ObjectOutputStream objectOutputStream;

    @Getter(AccessLevel.PROTECTED)
    private final ObjectInputStream objectInputStream;

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

    private final PacketLinkMono linkChain = new PacketLinkMonoConnect(this);

    ClientConnection(long id, ClientPool pool, Socket socket) throws IOException {
        this.socket = socket;
        this.pool = pool;
        this.id = id;

        linkChain.setSuccessor(new PacketLinkMonoChat(this))
                .setSuccessor(new PacketLinkMonoHeartbeat(this))
                .setSuccessor(new PacketLinkMonoNickEdit(this));

        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.flush();

        objectInputStream = new ObjectInputStream(socket.getInputStream());

        startReceiving();
    }

    public void restartKillThread() {
        if(killThread != null) {
            killThread.interrupt();
        }

        // We need to receive a heart beat every 5 seconds to keep the heart beating, if it's not
        killThread = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ignored) {
                return;
            }

            disconnect();
            log.info("Failed to receive a heartbeat from: " + id + ", forcefully closing their connection now!");
        });

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
        return !isDead && !socket.isClosed() && objectOutputStream != null;
    }
}
