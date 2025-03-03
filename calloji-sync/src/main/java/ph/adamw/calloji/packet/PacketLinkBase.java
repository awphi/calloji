package ph.adamw.calloji.packet;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class PacketLinkBase {
    protected final PacketType type;

    @Getter
    private PacketLinkBase successor = null;

    protected PacketLinkBase() {
        if(getClass().isAnnotationPresent(PacketLinkType.class)) {
            this.type = getClass().getAnnotation(PacketLinkType.class).value();
        } else {
            this.type = null;
        }
    }

    public void handleLink(PacketType packet, JsonElement content) {
        if(type == null) {
            log.error(getClass().getSimpleName() + " is not annotated with " + PacketLinkType.class.getSimpleName() + " annotation and therefore will never handle packets!");
        }

        if(packet == type) {
            log.debug("Handling " + packet.name() + " " + content.toString() + " @ " + getClass().getSimpleName());
            handle(packet, content);
        } else if(successor != null) {
            successor.handleLink(packet, content);
        }
    }

    public abstract void handle(PacketType type, JsonElement content);

    public PacketLinkBase setSuccessor(PacketLinkBase t) {
        successor = t;
        return successor;
    }
}
