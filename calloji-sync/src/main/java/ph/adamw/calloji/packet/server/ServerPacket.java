package ph.adamw.calloji.packet.server;

import lombok.AllArgsConstructor;
import ph.adamw.calloji.packet.Packet;

@AllArgsConstructor
public abstract class ServerPacket extends Packet {
	protected final long clientId;

	public abstract void handle(IServer server);
}
