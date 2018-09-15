package ph.adamw.calloji.packet.server;

public interface IClientConnection {
	void onHeartbeat();

	void onDisconnect();

	void onChatReceived(String message);

	void onNickEditRequest(String req);
}
