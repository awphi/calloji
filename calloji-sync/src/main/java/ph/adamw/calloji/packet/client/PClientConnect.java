package ph.adamw.calloji.packet.client;

public class PClientConnect extends PClient {
	@Override
	public void handle(IClient client) {
		client.onConnected();
	}
}
