package ph.adamw.calloji.client;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.chain.*;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.server.PSDisconnect;
import ph.adamw.calloji.packet.server.PS;
import ph.adamw.calloji.util.JsonUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class ClientRouter {
	private final PacketLink linkChain;

	private final Socket socket = new Socket();

	@Setter
	@Getter
	private long pid;

	private ObjectInputStream objectInputStream;
	private ObjectOutputStream objectOutputStream;

	public ClientRouter() {
		linkChain = new PacketLinkConnect();
		linkChain.setSuccessor(new PacketLinkChat())
				.setSuccessor(new PacketLinkNickChange())
				.setSuccessor(new PacketLinkPlayerUpdate())
				.setSuccessor(new PacketLinkBoardUpdate())
				.setSuccessor(new PacketLinkTurnUpdate());
	}

	public void connect(String hostname, int port) throws IOException {
		socket.connect(new InetSocketAddress(hostname, port));

		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.flush();

		objectInputStream = new ObjectInputStream(socket.getInputStream());

		// Data receiving thread
		new Thread(this::receive).start();
	}

	void requestDisconnect() {
		send(new PSDisconnect());
	}

	public void forceDisconnect() {
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

				if (x instanceof String) {
					final JsonObject json = JsonUtils.parseJson((String) x).getAsJsonObject();
					linkChain.handleLink(PacketType.getPacket(json.get("packet_id").getAsInt()), json.get("data"));
				}
			} catch (IOException | ClassNotFoundException e) {
				if(!(e instanceof EOFException)) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isConnected() {
		return !socket.isClosed() && objectOutputStream != null;
	}

	public void send(PS packet) {
		try {
			objectOutputStream.writeObject(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
