package ph.adamw.calloji.packet.server;

public class PSHeartbeat extends PS {
	@Override
	public void handle(IClientConnection server) {
		server.onHeartbeat();
	}
}
