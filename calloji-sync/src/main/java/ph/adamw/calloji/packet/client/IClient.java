package ph.adamw.calloji.packet.client;

public interface IClient {
	void onConnected();

	void onDisconnectAcknowledged();

    void onChatReceived(String from, String message);

    void onNickChanged(String nick);
}
