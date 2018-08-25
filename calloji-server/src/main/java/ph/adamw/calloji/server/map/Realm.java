package ph.adamw.calloji.server.map;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Realm {
    @Getter
    private final String id;

    private List<ph.adamw.calloji.server.prop.Prop> props = new ArrayList<ph.adamw.calloji.server.prop.Prop>();
    private List<ph.adamw.calloji.server.prop.entity.Entity> entities = new ArrayList<ph.adamw.calloji.server.prop.entity.Entity>();

    public Realm(String id) {
        this.id = id;
    }

    public boolean addProp(ph.adamw.calloji.server.prop.Prop i) {
        return props.add(i);
    }

    public boolean addEntity(ph.adamw.calloji.server.prop.entity.Entity i) {
        return entities.add(i);
    }
}
