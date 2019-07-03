package ph.adamw.calloji.packet.data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Slf4j
public class Player implements Serializable {
    public int balance = 1500;

    public int boardPosition = 0;

    public int jailed = 0;

    public int getOutOfJails = 0;

    public final long id;

    public boolean isBankrupt = false;

    private final GamePiece gamePiece;

    public Player(GamePiece piece, long id) {
        this.gamePiece = piece;
        this.id = id;
    }

    public int getOwnedType(PlotType type, Board board) {
        int c = 0;

        for(PropertyPlot i : getOwnedPlots(board)) {
            if (i.getType() == type) {
                c++;
        }
        }

        return c;
    }

    public List<PropertyPlot> getOwnedPlots(Board board) {
        final List<PropertyPlot> result = new ArrayList<>();

        for(Plot i : board.getPlots()) {
            if(i instanceof PropertyPlot && ((PropertyPlot) i).getOwner() != null && ((PropertyPlot) i).getOwner().equals(id)) {
                result.add((PropertyPlot) i);
            }
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Player) {
            return id == ((Player) obj).getId();
        }

        return super.equals(obj);
    }
}
