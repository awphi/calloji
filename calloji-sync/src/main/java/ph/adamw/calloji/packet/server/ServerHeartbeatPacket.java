package ph.adamw.calloji.packet.server;

public class ServerHeartbeatPacket extends ServerPacket {
	public ServerHeartbeatPacket(long clientId) {
		super(clientId);
	}

	@Override
	public void handle(IServer server) {
		server.receivedHeartbeat(clientId);
	}
}
