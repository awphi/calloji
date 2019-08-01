package ph.adamw.calloji.server;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.server.console.command.CommandParser;
import ph.adamw.calloji.server.console.command.CommandRigDice;
import ph.adamw.calloji.server.connection.ClientPool;
import ph.adamw.calloji.server.console.MainConsole;
import ph.adamw.calloji.server.monopoly.MonoGame;

import java.io.IOException;
import java.net.ServerSocket;

@Log4j2
public class ServerRouter {
	private static ClientPool clientPool;
	private static ServerSocket socket;

	@Getter
	private final static CommandParser parser = new CommandParser();

	@Getter
	private static EventBus eventBus = new EventBus();

	@Getter
	private final static MonoGame game = new MonoGame();

	private static int port = 8080;

	public static void main(String[] args) {
		new Thread(() -> new MainConsole().start(), "Console").start();
		parser.register(new CommandRigDice());

		int capacity = 4;

		for(String i : args) {
			if(i.matches("--port=([0-9]+)")) {
				port = Integer.parseInt(i.split("=")[1]);
			}

			if(i.matches("--players=([0-9]+)")) {
				capacity = Integer.parseInt(i.split("=")[1]);
			}
		}

		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}

		clientPool = new ClientPool(capacity);

		log.info("Started server on port: " + port + ", client pool capacity: " + capacity);
		new Thread(ServerRouter::waitForNextConnection, "ConnAcc").start();
	}

	private static void waitForNextConnection() {
		log.info("Listening for new connections...");
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
