package ph.adamw.calloji.packet;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ph.adamw.calloji.util.JsonUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class PacketDispatcher {
    protected abstract ObjectOutputStream getObjectOutputStream();
    protected abstract ObjectInputStream getObjectInputStream();
    protected abstract void handleLink(PacketType packetType, JsonElement content);

    protected abstract boolean isConnected();

    protected void startReceiving() {
        new Thread(this::receive).start();
    }

    public void send(PacketType type, Object content) {
        final JsonObject parent = new JsonObject();

        parent.addProperty("packet_id", type.getId());

        if(content instanceof JsonElement) {
            parent.add("data", (JsonElement) content);
        } else {
            parent.add("data", JsonUtils.getJsonElement(content));
        }

        try {
            getObjectOutputStream().writeObject(parent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receive() {
        while(isConnected()) {
            try {
                final Object x = getObjectInputStream().readObject();

                if (x instanceof String) {
                    final JsonObject json = JsonUtils.parseJson((String) x).getAsJsonObject();
                    handleLink(PacketType.getPacket(json.get("packet_id").getAsInt()), json.get("data"));
                }
            } catch (IOException | ClassNotFoundException e) {
                if(!(e instanceof EOFException)) {
                    e.printStackTrace();
                }
            }
        }
    }
}
