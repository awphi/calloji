package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import ph.adamw.calloji.packet.data.Board;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonoBoard {
    @Getter
    private final Board board = new Board();

    @Getter
    private final Map<Plot, MonoPropertyPlot> monoPlots = new HashMap<>();

    MonoBoard(MonoGame game) {
        for(Plot i : board.getPlots()) {
            if(i instanceof StreetPlot) {
                monoPlots.put(i, new MonoStreetPlot(game, (StreetPlot) i));
            } else if(i instanceof PropertyPlot) {
                monoPlots.put(i, new MonoPropertyPlot(game, (PropertyPlot) i));
            }
        }
    }

    public Integer indexOfFirstPlot(PlotType e) {
        for(int i = 0; i < board.getPlots().size(); i ++) {
            if(board.getPlots().get(i).getType() == e) {
                return i;
            }
        }

        return null;
    }

    public MonoPropertyPlot getMonoPlot(Plot plot) {
        return monoPlots.get(plot);
    }

    public Plot getIndexedPlot(int index) {
        return board.getPlots().get(index);
    }
}
