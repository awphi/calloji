package ph.adamw.calloji.packet.data;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Log4j2
public class Player implements Serializable {
    public int balance = 1500;

    public int boardPosition = 0;

    public MoveType lastMoveType = MoveType.NONE;

    public boolean wasRoll = false;

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
            if(i instanceof PropertyPlot && ((Long) id).equals(((PropertyPlot) i).getOwner())) {
                result.add((PropertyPlot) i);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public List<StreetPlot> getOwnedStreetPlots(Board board) {
        // Janky casting but works
        return (List<StreetPlot>)(List<?>) getOwnedPlots(board).stream().filter(p -> p instanceof StreetPlot).collect(Collectors.toList());
    }

    public boolean hasMonopolyOf(PlotType type, Board board) {
        for(Plot i : board.getAllPlotsOfType(type)) {
            if(i instanceof PropertyPlot && !((Long) id).equals(((PropertyPlot) i).getOwner())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Player) {
            return id == ((Player) obj).getId();
        }

        return super.equals(obj);
    }
}
