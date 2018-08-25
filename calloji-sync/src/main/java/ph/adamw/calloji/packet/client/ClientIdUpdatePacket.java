package ph.adamw.calloji.packet.client;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ClientIdUpdatePacket extends ClientPacket {
	private final long clientId;

	@Override
	public void handle(IClient client) {
		client.setId(clientId);
	}
}
