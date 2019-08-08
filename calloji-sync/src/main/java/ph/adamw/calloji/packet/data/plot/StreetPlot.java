package ph.adamw.calloji.packet.data.plot;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.calloji.packet.data.Board;
import ph.adamw.calloji.packet.data.Player;
import ph.adamw.calloji.util.GameConstants;

public class StreetPlot extends PropertyPlot {
    @Getter
    @Setter
    private int houses = 0;

    @Getter
    private final int buildCost;

    public StreetPlot(String name, PlotType type, int value, int buildCost) {
        super(name, type, value);

        this.buildCost = buildCost;
    }

    @Override
    public boolean isBuiltOn() {
        return getHouses() > 0;
    }

    public int getRent() {
        final int y = (getValue() / 10) - 4;
        return y * GameConstants.HOUSE_MULTIPLIERS[houses];
    }

    public boolean canBuildHouse(Player player, Board board) {
        return player.hasMonopolyOf(getType(), board)
                && player.getBalance() >= getBuildCost()
                && board.isHouseChangeAcceptable(this, true)
                && getHouses() + 1 <= GameConstants.MAX_HOUSES;
    }

    public boolean canSellHouse(Player player, Board board) {
        return player.hasMonopolyOf(getType(), board)
                && board.isHouseChangeAcceptable(this, false)
                && isBuiltOn();
    }
}
