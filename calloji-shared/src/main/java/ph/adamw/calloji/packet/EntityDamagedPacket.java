package ph.adamw.calloji.packet;

public class EntityDamagedPacket extends Packet {
    private final long entityId;

    public EntityDamagedPacket(long clientId, long entityId) {
        super(clientId);

        this.entityId = entityId;
    }
}
