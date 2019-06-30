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
		// Establish logger defaults then instantiate everything that uses a logger
		LoggerUtils.setFormatting();
		LoggerUtils.setProperty("defaultLogLevel", "info");
		LoggerUtils.establishLevels(args);

		clientPool = new ClientPool(1);

		// Bound to the client pool (well technically all client pools) on instantiation via the eventbus
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
