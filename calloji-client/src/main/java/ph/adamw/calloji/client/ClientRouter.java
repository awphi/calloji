package ph.adamw.calloji.client;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.chain.*;
import ph.adamw.calloji.packet.PacketDispatcher;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.ConnectionUpdate;

import java.io.*;
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
	private InputStream inputStream;

	@Getter(AccessLevel.PROTECTED)
	private OutputStream outputStream;

	public ClientRouter() {
		linkChain.setSuccessor(new PacketLinkChat())
				.setSuccessor(new PacketLinkNickChange())
				.setSuccessor(new PacketLinkPlayerUpdate())
				.setSuccessor(new PacketLinkBoardUpdate())
				.setSuccessor(new PacketLinkTurnUpdate())
				.setSuccessor(new PacketLinkDiceRolled())
				.setSuccessor(new PacketLinkPlotLandedOn())
				.setSuccessor(new PacketLinkCardDrawn());
	}

	public void connect(String hostname, int port) throws IOException {
		if(isConnected()) {
			disconnectAndAlertServer();
		}

		socket.connect(new InetSocketAddress(hostname, port), 5000);

		outputStream = socket.getOutputStream();
		outputStream.flush();

		inputStream = socket.getInputStream();

		startReceiving();
	}

	public void disconnectAndAlertServer() {
		send(PacketType.CLIENT_CONNECTION_UPDATE, new ConnectionUpdate(true, getPid()));
		disconnect();
		//TODO unload board and stuff
	}

	public void disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Platform.runLater(() -> Client.getGui().getDisconnectButton().setDisable(true));
	}

	@Override
	protected void handleLink(PacketType packetType, JsonElement content) {
 		linkChain.handleLink(packetType, content);
	}

	@Override
	public boolean isConnected() {
		return !socket.isClosed() && outputStream != null;
	}
}
