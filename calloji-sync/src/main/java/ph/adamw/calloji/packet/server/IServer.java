package ph.adamw.calloji.packet.server;

public interface IServer {
	void receivedHeartbeat(long client);
}
