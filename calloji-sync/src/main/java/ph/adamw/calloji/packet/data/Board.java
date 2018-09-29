package ph.adamw.calloji.packet.data;

import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Serializable {
    @Getter
    private final List<Plot> plots = new ArrayList<>();

    public Plot plotAt(int i) {
        return getPlots().get(i);
    }
}
