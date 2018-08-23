package ph.adamw.calloji.prop.entity;

import ph.adamw.calloji.prop.entity.event.IDamagable;

public class EntityCow extends Entity implements IDamagable {
    private int health = 100;

    public void receiveDamage(int damage) {
        health -= damage;
    }
}
