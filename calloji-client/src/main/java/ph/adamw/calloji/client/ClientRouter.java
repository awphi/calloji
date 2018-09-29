package ph.adamw.calloji.client;

import com.google.gson.JsonElement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.chain.*;
import ph.adamw.calloji.packet.PacketDispatcher;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.ConnectionUpdate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class ClientRouter extends PacketDispatcher {
	private final PacketLinkBase linkChain = new PacketLinkConnect();

	private final Socket socket = new Socket();

	@Setter
	@Getter
	private long pid;

	@Getter(AccessLevel.PROTECTED)
	private ObjectInputStream objectInputStream;

	@Getter(AccessLevel.PROTECTED)
	private ObjectOutputStream objectOutputStream;

	public ClientRouter() {
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

		startReceiving();
	}

	void requestDisconnect() {
		send(PacketType.CLIENT_CONNECTION_UPDATE, new ConnectionUpdate(true, getPid()));
	}

	public void forceDisconnect() {
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
	public boolean isConnected() {
		return !socket.isClosed() && objectOutputStream != null;
	}
}
