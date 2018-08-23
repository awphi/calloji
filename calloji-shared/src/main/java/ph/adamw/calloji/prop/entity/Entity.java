package ph.adamw.calloji.prop.entity;

import lombok.Getter;
import ph.adamw.calloji.prop.Prop;

public abstract class Entity extends Prop {
    private static long nextEntityId = 0;

    @Getter
    private final long entityId;

    public Entity() {
        entityId = nextEntityId;
        nextEntityId ++;
    }
}
