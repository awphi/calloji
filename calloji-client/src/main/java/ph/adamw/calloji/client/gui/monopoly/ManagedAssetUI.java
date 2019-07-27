package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

public class ManagedAssetUI extends HBox {
    private final static Insets MARGIN_10 = new Insets(0, 10, 0, 0);
    private final Button mortgageButton;
    private final Button auctionButton;

    private final PropertyPlot plot;

    public ManagedAssetUI(PropertyPlot i) {
        this.plot = i;
        mortgageButton = new Button("Mortgage - £" + i.getValue() / 2 + ".00");
        auctionButton = new Button("Auction");

        setAlignment(Pos.CENTER);

        mortgageButton.setOnAction(event -> {
            //TODO send mortgage request packet, check for houses and if mortgaged on both client and server side
            //TODO on mortgage fulfilled change this into an UNmortgage button
        });

        auctionButton.setOnAction(event -> {
            boolean builtOn = false;

            if(i instanceof StreetPlot) {
                builtOn = ((StreetPlot) i).getHouses() > 0;
            }

            // Validate these client side AND server side
            if(!i.isMortgaged() && !builtOn) {
                Client.getRouter().send(PacketType.AUCTION_REQUEST, i);
            }
        });

        getChildren().add(new ThinPlotUI(i));
        getChildren().add(mortgageButton);
        getChildren().add(auctionButton);

        HBox.setMargin(mortgageButton, MARGIN_10);
    }

    public void setButtonsDisable(boolean b) {
        mortgageButton.setDisable(b);
        auctionButton.setDisable(b);
    }
}
