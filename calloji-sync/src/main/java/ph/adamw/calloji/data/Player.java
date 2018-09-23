package ph.adamw.calloji.data;

import lombok.Getter;
import lombok.Setter;
import ph.adamw.calloji.data.plot.Plot;
import ph.adamw.calloji.data.plot.PlotType;
import ph.adamw.calloji.data.plot.PropertyPlot;
import ph.adamw.calloji.data.plot.StreetPlot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player implements Serializable {
    @Getter
    @Setter
    private int balance = 200;

    @Getter
    @Setter
    private int boardPosition = 0;

    @Getter
    @Setter
    private int jailed = 0;

    @Getter
    @Setter
    private boolean isBankrupt = false;

    private final List<Plot> ownedPlots = new ArrayList<>();

    public boolean isJailed() {
        return jailed > 0;
    }

    public int getAssetsSellValue() {
        int v = 0;
        for(Plot i : ownedPlots) {
            if(i instanceof PropertyPlot) {
                final PropertyPlot p = (PropertyPlot) i;
                v += p.getValue() / 2;
            }
        }

        return v;
    }

    public int getBuildingsSellValue() {
        int v = 0;
        for(Plot i : ownedPlots) {
            if(i instanceof StreetPlot) {
                final StreetPlot y = (StreetPlot) i;
                v += y.getHouses() * (y.getBuildCost() / 2);
            }
        }

        return v;
    }

    public int getOwnedType(PlotType type) {
        int c = 0;

        for(Plot i : ownedPlots) {
            if(i.getType() == type) {
                c ++;
            }
        }

        return c;
    }
}
