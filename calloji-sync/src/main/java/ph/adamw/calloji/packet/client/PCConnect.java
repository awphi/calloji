package ph.adamw.calloji.packet.client;

public class PCConnect extends PC {
	@Override
	public void handle(IClient client) {
		client.onConnected();
	}
}
