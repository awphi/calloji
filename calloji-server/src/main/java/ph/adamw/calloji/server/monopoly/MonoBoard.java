package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import ph.adamw.calloji.packet.data.Board;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonoBoard {
    @Getter
    private final Board board = new Board();

    private final List<MonoPropertyPlot> plots = new ArrayList<>();

    MonoBoard(MonoGame game) {
        for(Plot i : board.getPlots()) {
            if(i instanceof StreetPlot) {
                plots.add(new MonoStreetPlot(game, (StreetPlot) i));
            } else if(i instanceof PropertyPlot) {
                plots.add(new MonoPropertyPlot(game, (PropertyPlot) i));
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

    public MonoPropertyPlot getMonoPlot(PropertyPlot plot) {
        for(MonoPropertyPlot i : plots) {
            if(i.getPlot() == plot) {
                return i;
            }
        }

        return null;
    }
}
