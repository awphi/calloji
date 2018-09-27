package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.PacketType;

@Slf4j
public abstract class PacketLink {
    private final PacketType type;

    private PacketLink successor = null;

    @SuppressWarnings("WeakerAccess")
    public PacketLink(PacketType type) {
        this.type = type;
    }

    public void handleLink(PacketType packet, JsonElement content) {
        if(packet == type) {
            log.debug("Handling " + packet.name() + " " + content.toString() + " @ " + getClass().getSimpleName());
            handle(type, content);
        } else if(successor != null) {
            successor.handleLink(packet, content);
        }
    }

    public abstract void handle(PacketType type, JsonElement content);

    public PacketLink setSuccessor(PacketLink t) {
        successor = t;
        return successor;
    }
}
