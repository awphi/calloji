package ph.adamw.calloji.server.connection;

import lombok.Getter;
import lombok.extern.java.Log;
import ph.adamw.calloji.packet.client.ClientPacket;
import ph.adamw.calloji.packet.server.ServerPacket;
import ph.adamw.calloji.server.ServerRouter;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

@Log
public class ClientConnection {
    @Getter
    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;

    private Thread killThread;
    private boolean isDead = false;

    private final ClientPool pool;
    private final long id;

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
                if (x instanceof ServerPacket) {
                    ((ServerPacket) x).handle(ServerRouter.getServer());
                }

            } catch (IOException | ClassNotFoundException e) {
                if(!(e instanceof EOFException || e instanceof SocketException)) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void send(ClientPacket o) {
        try {
            objectOutputStream.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void beatHeart() {
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

            log.fine("Failed to receive a heartbeat from: " + id + ", closing their connection now!");

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
}
