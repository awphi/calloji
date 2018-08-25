package ph.adamw.calloji.server.prop;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.adamw.calloji.server.map.Position;

import java.io.Serializable;

@NoArgsConstructor
public abstract class Prop implements Serializable {
    @Getter
    @Setter
    private Position position;
}
