package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.HouseRequest;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;
import ph.adamw.calloji.util.GameConstants;

public class ManagedAssetUI extends VBox {
    private final static Insets PADDING = new Insets(5, 5, 5, 5);
    private final static Insets MARGIN_H10 = new Insets(0, 20, 0, 0);

    private final Button mortgageButton = new Button("Mortgage - £0.00");
    private final Button auctionButton = new Button("Auction");

    private final Button buildHouseButton = new Button("Build House - £0.00");
    private final Button sellHouseButton = new Button("Sell House - £0.00");

    @Getter
    private PropertyPlot plot;

    public ManagedAssetUI(PropertyPlot i) {
        setAlignment(Pos.CENTER);

        final HBox mortgageAuctionBox = new HBox();
        HBox.setMargin(mortgageButton, MARGIN_H10);

        mortgageButton.setOnAction(event ->
                Client.getRouter().send(PacketType.MORTGAGE_REQUEST, i));

        auctionButton.setOnAction(event ->
                Client.getRouter().send(PacketType.AUCTION_REQUEST, i));

        mortgageAuctionBox.getChildren().addAll(mortgageButton, auctionButton);
        mortgageAuctionBox.setPadding(PADDING);


        if(i instanceof StreetPlot) {
            final HBox houseBox = new HBox();
            final StreetPlot sp = (StreetPlot) i;

            HBox.setMargin(buildHouseButton, MARGIN_H10);
            HBox.setMargin(sellHouseButton, MARGIN_H10);
            houseBox.getChildren().addAll(buildHouseButton, sellHouseButton);

            buildHouseButton.setText("Build House - £" + sp.getBuildCost() + ".00");
            sellHouseButton.setText("Sell House - £" + (sp.getBuildCost() / 2) + ".00");

            buildHouseButton.setOnAction(event ->
                    Client.getRouter().send(PacketType.HOUSE_REQUEST, new HouseRequest(sp, true)));

            sellHouseButton.setOnAction(event ->
                    Client.getRouter().send(PacketType.HOUSE_REQUEST, new HouseRequest(sp, false)));

            houseBox.setPadding(PADDING);
            getChildren().add(houseBox);
        }

        final ThinPlotUI plotUI = new ThinPlotUI(i);

        getChildren().add(0, mortgageAuctionBox);
        getChildren().add(0, plotUI);

        load(i);
    }

    // Acts as the client-side validation (also validated server side of course)
    public void setButtonsDisable(boolean b) {
        mortgageButton.setDisable(b);
        auctionButton.setDisable(b || plot.isMortgaged());

        if(plot instanceof StreetPlot) {
            final StreetPlot sp = (StreetPlot) plot;

            final boolean hasMonopoly = Client.getCache().getPlayer().getPlayer().hasMonopolyOf(plot.getType(), Client.getCache().getBoard());
            final boolean canAffordHouse = Client.getCache().getPlayer().getPlayer().getBalance() >= sp.getBuildCost();
            final boolean isBuiltOn = plot.isBuiltOn();

            sellHouseButton.setDisable(b || (!hasMonopoly || !isBuiltOn || !Client.getCache().getBoard().canConstructOn(sp, false)));
            buildHouseButton.setDisable(b || (!hasMonopoly || !(sp.getHouses() + 1 <= GameConstants.MAX_HOUSES) || !canAffordHouse || !Client.getCache().getBoard().canConstructOn(sp, true)));
        }
    }

    public void load(PropertyPlot i) {
        this.plot = i;

        if(i.isMortgaged()) {
            mortgageButton.setText("Unmortgage - £" + i.getUnmortgageCost() + ".00");
        } else {
            mortgageButton.setText("Mortgage - £" + i.getValue() / 2 + ".00");
        }

        setButtonsDisable(false);
    }
}
