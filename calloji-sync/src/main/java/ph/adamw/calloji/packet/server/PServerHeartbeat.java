package ph.adamw.calloji.packet.server;

public class PServerHeartbeat extends PServer {
	@Override
	public void handle(IClientConnection server) {
		server.onHeartbeat();
	}
}
