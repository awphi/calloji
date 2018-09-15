package ph.adamw.calloji.server;

import lombok.Getter;
import ph.adamw.calloji.server.connection.ClientPool;
import ph.adamw.calloji.util.LoggerUtils;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerRouter {
	@Getter
	private static ClientPool clientPool;

	private static final ServerSocket socket;

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

		clientPool = new ClientPool(12);

		new Thread(ServerRouter::waitForNextConnection).start();
	}

	private static void waitForNextConnection() {
		while (!socket.isClosed()) {
			try {
				if(clientPool.getCapacity() > clientPool.getConnected()) {
					clientPool.addConnection(socket.accept());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
