package ph.adamw.calloji.client;

import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.client.IClient;
import ph.adamw.calloji.packet.client.PClient;
import ph.adamw.calloji.packet.server.PServerDisconnect;
import ph.adamw.calloji.packet.server.PServer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class ClientRouter {
	private final IClient clientPacketHandler = new ClientPacketHandler(this);

	private final Socket socket = new Socket();

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	//TODO implement server choice w/ passwords etc.
	public void connect() throws IOException {
		socket.connect(new InetSocketAddress("0.0.0.0", 8080));

		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.flush();

		objectInputStream = new ObjectInputStream(socket.getInputStream());

		// Data receiving thread
		new Thread(this::receive).start();
	}

	public void requestDisconnect() {
		send(new PServerDisconnect());
	}

	void forceDisconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receive() {
		while(!socket.isClosed()) {
			try {
				final Object x = objectInputStream.readObject();

				if (x instanceof PClient) {
					((PClient) x).handle(clientPacketHandler);
				}
			} catch (IOException | ClassNotFoundException e) {
				if(!(e instanceof EOFException)) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isConnected() {
		return !socket.isClosed();
	}

	public void send(PServer packet) {
		try {
			objectOutputStream.writeObject(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
