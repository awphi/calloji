package ph.adamw.calloji.packet.server;

public class PSDisconnect extends PS {
	@Override
	public void handle(IClientConnection server) {
		server.onDisconnect();
	}
}
