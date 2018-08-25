package ph.adamw.calloji.server;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.impl.SimpleLogger;
import ph.adamw.calloji.server.connection.ClientPool;
import ph.adamw.calloji.util.LoggerUtils;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerRouter {
	static {
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "[dd/MM/yy HH:mm:ss]");
		System.setProperty("org.slf4j.simpleLogger.levelInBrackets", "true");
	}

	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ServerRouter.class);

	@Getter
	private static final Server server = new Server();

	private static final ServerSocket socket;

	@Getter
	private static final ClientPool clientPool = new ClientPool(12);

	static {
		try {
			socket = new ServerSocket(8080);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static void main(String[] args) {
		log.info("ServerRouter started!");

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
