package ph.adamw.calloji.server.connection;

import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.client.PClient;
import ph.adamw.calloji.packet.server.*;
import ph.adamw.calloji.server.ServerRouter;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

@Slf4j
public class ClientConnection implements IClientConnection {
    @Getter
    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;

    private Thread killThread;
    private boolean isDead = false;

    private final ClientPool pool;
    private final long id;

    private String nick = "Calloji User";

    ClientConnection(long id, ClientPool pool, Socket socket) throws IOException {
        this.socket = socket;
        this.pool = pool;
        this.id = id;

        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.flush();

        objectInputStream = new ObjectInputStream(socket.getInputStream());

        // Data receiving thread
        new Thread(this::receive).start();
    }

    private void receive() {
        while(!isDead) {
            try {
                final Object x = objectInputStream.readObject();
                if (x instanceof PServer) {
                    final PServer inc = (PServer) x;
                    inc.handle(this);
                }

            } catch (IOException | ClassNotFoundException e) {
                if(!(e instanceof EOFException || e instanceof SocketException)) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void send(PClient o) {
        try {
            objectOutputStream.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHeartbeat() {
        log.debug("Received heartbeat from client: " + id);

        if(killThread != null) {
            killThread.interrupt();
        }

        // We need to receive a heart beat every 5 seconds to keep the heart beating, if it's not
        killThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
                return;
            }

            log.info("Failed to receive a heartbeat from: " + id + ", forcefully closing their connection now!");

            pool.removeConnection(id);
            isDead = true;

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if(!isDead) {
            killThread.start();
        }
    }

    @Override
    public void onDisconnect() {
        log.debug("Disconnecting client peacefully: " + id);

        if(killThread != null) {
            killThread.interrupt();
        }

        ServerRouter.getClientPool().removeConnection(id);

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        send(new PServerDisconnect.Ack());
    }

    @Override
    public void onChatReceived(String message) {
        // TODO spam filter etc -  if approved send to all clients like below, if not send a bad boy message to this client
        if(message.equals("::bank")) {
            message = "Hey, everyone, I just tried to do something very silly!";
        }

        for(ClientConnection c : pool.getImmutableConnections()) {
            c.send(new PServerChat.Ack(nick, message));
        }
    }

    @Override
    public void onNickEditRequest(String req) {
        //TODO validate against clientPool's nicks
        nick = req;
        send(new PServerNickEdit.Ack(nick));
    }
}
