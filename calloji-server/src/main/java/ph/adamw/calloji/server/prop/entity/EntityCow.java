package ph.adamw.calloji.server.prop.entity;

public class EntityCow extends Entity implements ph.adamw.calloji.server.prop.entity.event.IDamagable {
    private int health = 100;

    public void receiveDamage(int damage) {
        health -= damage;
    }
}
