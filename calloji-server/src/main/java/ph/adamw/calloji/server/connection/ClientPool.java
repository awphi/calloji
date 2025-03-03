package ph.adamw.calloji.server.connection;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.data.ConnectionUpdate;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.event.ClientConnectedEvent;
import ph.adamw.calloji.server.connection.event.ClientDisconnectedEvent;

import java.io.IOException;
import java.net.Socket;
import java.util.TreeMap;
import java.util.UUID;

@Log4j2
public class ClientPool {
	private final TreeMap<Long, ClientConnection> map = new TreeMap<>();

	@Getter
	private final int capacity;

	public ClientPool(int capacity) {
		super();
		this.capacity = capacity;
	}

	public ClientConnection get(long id) {
		return map.get(id);
	}

	public int getConnected() {
		return map.values().size();
	}

	private long getNextClientId() {
		if(map.keySet().size() >= capacity) {
			return -1;
		}

		long id;

		do {
			id = UUID.randomUUID().getMostSignificantBits();
		} while(map.keySet().contains(id));

		return id;
	}

	public ImmutableSet<ClientConnection> getImmutableConnections() {
		return new ImmutableSet.Builder<ClientConnection>().addAll(map.values()).build();
	}

	public ClientConnection addConn(Socket conn) {
		final long clientId = getNextClientId();

		if(clientId == -1) {
			throw new RuntimeException("All client IDs are occupied! Did you make sure the pool is not full when adding a new connection?");
		}

		log.info("Received connection from: " + conn.getInetAddress().toString() + ", assigned ID: " + clientId);
		final ClientConnection cc;

		try {
			cc = new ClientConnection(clientId, this, conn);
		} catch (IOException e) {
			log.trace(e);
			return null;
		}

		final ClientConnection x = map.put(clientId, cc);
		cc.restartKillThread();

		cc.send(PacketType.CLIENT_CONNECTION_UPDATE, new ConnectionUpdate(false, clientId));
		ServerRouter.getEventBus().post(new ClientConnectedEvent(clientId, this));

		return x;

	}

	public boolean removeConn(long id) {
		ServerRouter.getEventBus().post(new ClientDisconnectedEvent(id, this));
		return map.remove(id) != null;
	}
}
