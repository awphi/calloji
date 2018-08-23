package ph.adamw.calloji.client;

import ph.adamw.calloji.prop.Prop;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    private static final Socket socket = new Socket();

    private static ObjectInputStream objectInputStream;
    private static ObjectOutputStream objectOutputStream;

    public static void main(String[] args) throws IOException {
        socket.connect(new InetSocketAddress("0.0.0.0", 8080));

        objectInputStream = new ObjectInputStream(socket.getInputStream());
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

        // Data receiving thread
        new Thread(Client::receiveData).start();
    }

    private static void receiveData() {
        System.out.println("yy");

        while(socket.isConnected()) {
            try {
                System.out.println(objectInputStream.available());
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                final Object x = objectInputStream.readObject();
                if (x instanceof Prop) {
                    System.out.println(x);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            System.out.println("x");
        }
    }
}
