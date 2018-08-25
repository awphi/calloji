package ph.adamw.calloji.client;

import lombok.extern.java.Log;
import ph.adamw.calloji.packet.client.ClientPacket;
import ph.adamw.calloji.packet.server.ServerHeartbeatPacket;
import ph.adamw.calloji.packet.server.ServerPacket;
import ph.adamw.calloji.util.LoggerUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;

@Log
public class ClientRouter {
	private static final Client client = new Client();

	private static final Socket socket = new Socket();
	private static ObjectInputStream objectInputStream;
	private static ObjectOutputStream objectOutputStream;

	public static void main(String[] args) throws IOException {
		log.setLevel(Level.SEVERE);
		LoggerUtils.establishLevels(args, log);

		socket.connect(new InetSocketAddress("0.0.0.0", 8080));

		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.flush();

		objectInputStream = new ObjectInputStream(socket.getInputStream());

		// Data receiving thread
		new Thread(ClientRouter::receive).start();

		// Heartbeat thread
		new Thread(() -> {
			while(client.getId() == null) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}

			while(socket.isConnected()) {
				ClientRouter.send(new ServerHeartbeatPacket(client.getId()));
				System.out.println("Sent heartbeat to server.");

				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private static void receive() {
		while(socket.isConnected()) {
			try {
				final Object x = objectInputStream.readObject();

				if (x instanceof ClientPacket) {
					((ClientPacket) x).handle(client);
				}
			} catch (IOException | ClassNotFoundException e) {
				if(!(e instanceof EOFException)) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void send(ServerPacket packet) {
		try {
			objectOutputStream.writeObject(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
