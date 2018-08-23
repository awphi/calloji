package ph.adamw.calloji.server;

import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection {
    private static long nextClientId = 0;

    private final long clientId;

    @Getter
    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;
    private final ObjectInputStream objectInputStream;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;

        clientId = nextClientId;
        nextClientId ++;

        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    }

    public void sendObject(Object o) {
        try {
            objectOutputStream.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object receivePacket() {
        try {
            if(objectInputStream.available() > 0) {
                return objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
