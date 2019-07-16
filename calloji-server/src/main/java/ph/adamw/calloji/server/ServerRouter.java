package ph.adamw.calloji.server;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import ph.adamw.calloji.server.connection.ClientPool;
import ph.adamw.calloji.server.monopoly.MonoGame;
import ph.adamw.calloji.util.LoggerUtils;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerRouter {
	private static ClientPool clientPool;
	private static final ServerSocket socket;

	@Getter
	private static MonoGame game;

	@Getter
	private static EventBus eventBus = new EventBus();

	static {
		try {
			socket = new ServerSocket(8080);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static void main(String[] args) {
		// Must init the logger before instantiating objects that use it
		LoggerUtils.init(args);

		clientPool = new ClientPool(2);
		game = new MonoGame();

		new Thread(ServerRouter::waitForNextConnection).start();
	}

	private static void waitForNextConnection() {
		while (!socket.isClosed()) {
			try {
				if(clientPool.getCapacity() > clientPool.getConnected()) {
					clientPool.addConn(socket.accept());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
