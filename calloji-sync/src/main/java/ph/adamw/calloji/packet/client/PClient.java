package ph.adamw.calloji.packet.client;

import ph.adamw.calloji.packet.Packet;

public abstract class PClient extends Packet {
	public abstract void handle(IClient client);
}
