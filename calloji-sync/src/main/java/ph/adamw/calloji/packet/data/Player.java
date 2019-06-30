package ph.adamw.calloji.packet.data;

import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Player implements Serializable {
    public int balance = 1500;

    public int boardPosition = 0;

    public int jailed = 0;

    public int getOutOfJails = 0;

    public boolean isBankrupt = false;

    private final GamePiece gamePiece;

    public Player(GamePiece piece) {
        this.gamePiece = piece;
    }

    public int getOwnedType(PlotType type, Board board) {
        int c = 0;

        for(Plot i : board.getPlots()) {
            if(i instanceof PropertyPlot) {
                if (i.getType() == type  && ((PropertyPlot) i).getOwner().equals(this)) {
                    c++;
                }
            }
        }

        return c;
    }

    /*
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Player) {
            return gamePiece == ((Player) obj).getGamePiece();
        }

        return super.equals(obj);
    }
    */
}
