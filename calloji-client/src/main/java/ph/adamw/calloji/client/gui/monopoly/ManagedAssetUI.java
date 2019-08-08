package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.HouseRequest;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

@Log4j2
public class ManagedAssetUI extends VBox {
    private final static Insets PADDING = new Insets(5, 5, 5, 5);
    private final static Insets MARGIN_H10 = new Insets(0, 20, 0, 0);

    private final Button mortgageButton = new Button("Mortgage - £0.00");
    private final Button auctionButton = new Button("Auction");

    private final Button buildHouseButton = new Button("Build House - £0.00");
    private final Button sellHouseButton = new Button("Sell House - £0.00");

    @Getter
    @Setter
    private boolean forcedManagement = false;

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
        mortgageButton.setDisable(b || plot.isBuiltOn());
        auctionButton.setDisable(b || forcedManagement || plot.isMortgaged());

        if(plot instanceof StreetPlot) {
            final StreetPlot sp = (StreetPlot) plot;
            sellHouseButton.setDisable(b ||
                    !sp.canSellHouse(Client.getCache().getPlayer().getPlayer(), Client.getCache().getBoard()));

            buildHouseButton.setDisable(b || forcedManagement ||
                    !sp.canBuildHouse(Client.getCache().getPlayer().getPlayer(), Client.getCache().getBoard()));
        }
    }

    // Load is ran on a board update i.e the state of this plot has changed
    public void load(PropertyPlot i) {
        this.plot = i;

        if(i.isMortgaged()) {
            mortgageButton.setText("Unmortgage - £" + i.getUnmortgageCost() + ".00");
        } else {
            mortgageButton.setText("Mortgage - £" + i.getValue() / 2 + ".00");
        }

        // Invalidates the buttons enabled states with the new plot
        setButtonsDisable(false);
    }
}
