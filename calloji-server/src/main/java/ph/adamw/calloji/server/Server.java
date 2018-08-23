package ph.adamw.calloji.server;
import lombok.Getter;
import ph.adamw.calloji.prop.entity.EntityCow;
import ph.adamw.calloji.server.packet.PacketManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private static final ServerSocket socket;

    @Getter
    private static final Set<ClientConnection> connections = new HashSet<>();

    static {
        try {
            socket = new ServerSocket(8080);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        //TODO load map from file (made in map editor) here
        new Thread(() -> {
            try {
                waitForNextConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        PacketManager.listenForPackets();
    }

    private static void addConnection(Socket conn) {
        System.out.println("Received connection from: " + conn.getInetAddress().toString());
        final ClientConnection cc;

        try {
            cc = new ClientConnection(conn);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        connections.add(cc);

        // Send them relevant information to load world
        //e.g. this loads a cow near them
        cc.sendObject(new EntityCow());
    }

    private static void removeConnection(Socket conn) {
        for(ClientConnection c : connections) {
            if(c.getSocket() == conn) {
                connections.remove(c);
                return;
            }
        }
    }

    private static void waitForNextConnection() throws IOException {
        Socket rec = socket.accept();

        synchronized (connections) {
            addConnection(rec);
            System.out.println(connections.size());
        }

        System.out.println(connections.size());

        synchronized (connections) {
            System.out.println(connections.size());
        }

        waitForNextConnection();
    }
}
