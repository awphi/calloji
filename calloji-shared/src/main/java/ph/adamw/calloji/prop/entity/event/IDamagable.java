package ph.adamw.calloji.prop.entity.event;

public interface IDamagable extends IEventListener {
    void receiveDamage(int damage);
}
