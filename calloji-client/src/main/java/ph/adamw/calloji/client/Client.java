package ph.adamw.calloji.client;

import ph.adamw.calloji.packet.client.IClient;

public class Client implements IClient {
    private Long id = null;

    @Override
    public void setId(long id) {
        System.out.println("Received new client ID from server: " + id);
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
