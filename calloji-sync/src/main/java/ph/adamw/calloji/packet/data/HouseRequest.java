package ph.adamw.calloji.packet.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

@AllArgsConstructor
@Getter
public class HouseRequest {
    private final StreetPlot plot;
    private final boolean isBuild;
}
