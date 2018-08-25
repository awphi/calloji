package ph.adamw.calloji.server.prop.entity.event;

public interface IDamagable extends IEventListener {
    void receiveDamage(int damage);
}
