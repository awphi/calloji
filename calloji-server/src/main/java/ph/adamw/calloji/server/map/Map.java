package ph.adamw.calloji.server.map;

import java.util.ArrayList;

public class Map extends ArrayList<Realm> {
    public Realm getRealmById(String id) {
        for(Realm i : this) {
            if(i.getId().equals(id)) {
                return i;
            }
        }

        return null;
    }

    @Override
    public boolean add(Realm r) {
        if(getRealmById(r.getId()) == null) {
            return super.add(r);
        }

        return false;
    }
}
