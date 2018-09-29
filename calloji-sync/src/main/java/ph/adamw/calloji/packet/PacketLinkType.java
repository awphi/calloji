package ph.adamw.calloji.packet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface PacketLinkType {
    PacketType value();
}
