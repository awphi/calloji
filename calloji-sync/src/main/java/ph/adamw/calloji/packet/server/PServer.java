package ph.adamw.calloji.packet.server;

import ph.adamw.calloji.packet.Packet;

public abstract class PServer extends Packet {
	public abstract void handle(IClientConnection server);
}
