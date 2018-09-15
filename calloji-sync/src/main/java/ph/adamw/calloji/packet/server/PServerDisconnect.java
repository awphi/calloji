package ph.adamw.calloji.packet.server;

import ph.adamw.calloji.packet.client.IClient;
import ph.adamw.calloji.packet.client.PClient;

public class PServerDisconnect extends PServer {
	@Override
	public void handle(IClientConnection server) {
		server.onDisconnect();
	}

	// Acknowledgment packet classes are generally internal classes to that of the packet they are in response to
	// this is more of a personal choice but it clutters the names less.

	// Ack packets are used to send the result of an input after validation or calculations i.e. validating
	// a user can currently roll the dice or play a certain card etc.

	// However, in this example case it holds no data aside from its existence since there is no validation to be done
	// on a onDisconnect request.
	public static class Ack extends PClient {
		@Override
		public void handle(IClient client) {
			client.onDisconnectAcknowledged();
		}
	}
}
