package ph.adamw.calloji.map;

import lombok.Getter;
import ph.adamw.calloji.prop.Prop;
import ph.adamw.calloji.prop.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class Realm {
    @Getter
    private final String id;

    private List<Prop> props = new ArrayList<Prop>();
    private List<Entity> entities = new ArrayList<Entity>();

    public Realm(String id) {
        this.id = id;
    }

    public boolean addProp(Prop i) {
        return props.add(i);
    }

    public boolean addEntity(Entity i) {
        return entities.add(i);
    }
}
