package ph.adamw.calloji.packet.data;

import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board implements Serializable {
    public Board() {
        Collections.addAll(plots,
                Plot.getCommonInstance(PlotType.GO),
                new StreetPlot("Old Kent Road", PlotType.BROWN, 60, 50),
                Plot.getCommonInstance(PlotType.COMMUNITY_CHEST),
                new StreetPlot("Whitechapel Road", PlotType.BROWN, 60, 50),
                new Plot("Income Tax", PlotType.TAX),
                new PropertyPlot("King's Cross Station", PlotType.STATION, 200),
                new StreetPlot("The Angel, Islington", PlotType.LIGHT_BLUE, 100, 50),
                Plot.getCommonInstance(PlotType.CHANCE),
                new StreetPlot("Euston Road", PlotType.LIGHT_BLUE, 100, 50),
                new StreetPlot("Pentonville Road", PlotType.LIGHT_BLUE, 120, 50),
                new Plot("Jail", PlotType.JAIL),
                new StreetPlot("Pall Mall", PlotType.PINK, 140, 100),
                new PropertyPlot("Electric Company", PlotType.UTILITY, 150),
                new StreetPlot("Whitehall", PlotType.PINK, 140, 100),
                new StreetPlot("Northumberland Avenue", PlotType.PINK, 160, 100),
                new PropertyPlot("Marylebone Station", PlotType.STATION, 200),
                new StreetPlot("Bow Street", PlotType.ORANGE, 180, 100),
                Plot.getCommonInstance(PlotType.COMMUNITY_CHEST),
                new StreetPlot("Marlborough Street", PlotType.ORANGE, 180, 100),
                new StreetPlot("Vine Street", PlotType.ORANGE, 200, 100),
                new Plot("Free Parking", PlotType.FREE_PARKING),
                new StreetPlot("Strand", PlotType.RED, 220, 150),
                Plot.getCommonInstance(PlotType.CHANCE),
                new StreetPlot("Fleet Street", PlotType.RED, 220, 150),
                new StreetPlot("Trafalgar Square", PlotType.RED, 240, 150),
                new PropertyPlot("Fenchurch Street", PlotType.STATION, 200),
                new StreetPlot("Leicester Square", PlotType.YELLOW, 260, 150),
                new StreetPlot("Coventry Street", PlotType.YELLOW, 260, 150),
                new PropertyPlot("Water Works", PlotType.UTILITY, 150),
                new StreetPlot("Piccadilly", PlotType.YELLOW, 280, 150),
                new Plot("Go To Jail", PlotType.GO_TO_JAIL),
                new StreetPlot("Regent Street", PlotType.GREEN, 300, 200),
                new StreetPlot("Oxford Street", PlotType.GREEN, 300, 200),
                Plot.getCommonInstance(PlotType.COMMUNITY_CHEST),
                new StreetPlot("Bond Street", PlotType.GREEN, 320, 200),
                new PropertyPlot("Liverpool Street Station", PlotType.STATION, 200),
                Plot.getCommonInstance(PlotType.CHANCE),
                new StreetPlot("Park Lane", PlotType.BLUE, 350, 200),
                new Plot("Super Tax", PlotType.SUPER_TAX),
                new StreetPlot("Mayfair", PlotType.BLUE, 400, 200));
    }

    @Getter
    private final List<Plot> plots = new ArrayList<>();

    public Plot plotAt(int i) {
        return getPlots().get(i);
    }
}
