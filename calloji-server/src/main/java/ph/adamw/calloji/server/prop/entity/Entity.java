package ph.adamw.calloji.server.prop.entity;

import lombok.Getter;

public abstract class Entity extends ph.adamw.calloji.server.prop.Prop {
    private static long nextEntityId = 0;

    @Getter
    private final long entityId;

    public Entity() {
        entityId = nextEntityId;
        nextEntityId ++;
    }
}
