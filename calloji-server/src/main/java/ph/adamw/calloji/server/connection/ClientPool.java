package ph.adamw.calloji.server.connection;

import lombok.Getter;
import ph.adamw.calloji.packet.client.ClientIdUpdatePacket;

import java.io.IOException;
import java.net.Socket;
import java.util.TreeMap;

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
		for(long i = 0; i < capacity; i ++) {
			if(!map.containsKey(i)) {
				return i;
			}
		}

		return -1;
	}

	public ClientConnection addConnection(Socket conn) {
		System.out.println("Received connection from: " + conn.getInetAddress().toString());
		final ClientConnection cc;

		final long clientId = getNextClientId();

		if(clientId == -1) {
			throw new RuntimeException("All client IDs are occupied! Did you make sure the pool is not full when adding a new connection?");
		}

		try {
			cc = new ClientConnection(clientId, this, conn);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		cc.send(new ClientIdUpdatePacket(clientId));

		return map.put(clientId, cc);
	}

	public boolean removeConnection(long id) {
		return map.remove(id) != null;
	}
}
