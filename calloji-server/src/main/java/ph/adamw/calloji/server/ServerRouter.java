package ph.adamw.calloji.server;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ph.adamw.calloji.server.command.Command;
import ph.adamw.calloji.server.command.CommandParser;
import ph.adamw.calloji.server.command.CommandRigDice;
import ph.adamw.calloji.server.connection.ClientPool;
import ph.adamw.calloji.server.monopoly.MonoGame;
import ph.adamw.calloji.util.LoggerUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Scanner;

public class ServerRouter {
	private static ClientPool clientPool;
	private static ServerSocket socket;
	private static CommandParser parser;

	@Getter
	private static MonoGame game;

	private static int port = 8080;

	private static Logger log;

	@Getter
	private static EventBus eventBus = new EventBus();

	public static void main(String[] args) {
		// Must init the logger before instantiating objects that use it via @Slf4j
		LoggerUtils.init(args);
		log = LoggerFactory.getLogger(ServerRouter.class);

		parser = new CommandParser();
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
		game = new MonoGame();

		log.info("Started server on port: " + port + ", client pool capacity: " + capacity);
		new Thread(ServerRouter::waitForNextConnection, "Connection Acceptor").start();
		new Thread(ServerRouter::readInput, "Console").start();
	}

	private static void readInput() {
		final Scanner scanner = new Scanner(System.in);
		while(true) {
			final String in = scanner.nextLine();
			final String[] split = in.split(" ");
			final String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];

			final Command cmd = parser.getCommand(split[0]);
			if(cmd != null) {
				cmd.accept(args);
			} else {
				log.info("Unrecognized command: " + in);
			}
		}
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
