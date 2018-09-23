package ph.adamw.calloji.packet.client;

import ph.adamw.calloji.data.Board;
import ph.adamw.calloji.data.UIState;

public interface IClient {
	void onConnected();

	void onDisconnectAcknowledged();

    void onChatReceived(String from, String message);

    void onNickChanged(String nick);

    void onBoardUpdate(Board board);

    void onUIUpdate(UIState state);
}
