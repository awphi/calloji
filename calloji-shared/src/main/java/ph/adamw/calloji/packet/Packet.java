package ph.adamw.calloji.packet;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Packet {
    private final long clientId;
}
