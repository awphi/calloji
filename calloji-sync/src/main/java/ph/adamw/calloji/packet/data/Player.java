package ph.adamw.calloji.packet.data;

import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Player implements Serializable {
    public int balance = 200;

    public int boardPosition = 0;

    public int jailed = 0;

    public boolean isBankrupt = false;

    protected final List<Plot> ownedPlots = new ArrayList<>();

    private final GamePiece gamePiece;

    public Player(GamePiece piece) {
        this.gamePiece = piece;
    }

    public int getOwnedType(PlotType type) {
        int c = 0;

        for(Plot i : getOwnedPlots()) {
            if(i.getType() == type) {
                c ++;
            }
        }

        return c;
    }
}
