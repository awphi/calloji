package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import lombok.Getter;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

public class ManagedAssetUI extends HBox {
    private final static Insets MARGIN_10 = new Insets(0, 10, 0, 0);
    private final Button mortgageButton;
    private final Button auctionButton;

    @Getter
    private PropertyPlot plot;

    public ManagedAssetUI(PropertyPlot i) {
        this.plot = i;
        mortgageButton = new Button("Mortgage - £" + i.getValue() / 2 + ".00");
        auctionButton = new Button("Auction");

        setAlignment(Pos.CENTER);

        mortgageButton.setOnAction(event -> {
            // Validate these client side AND server side
            if(!i.isBuiltOn()) {
                Client.getRouter().send(PacketType.MORTGAGE_REQUEST, i);
            }
        });

        auctionButton.setOnAction(event -> {
            // Validate these client side AND server side
            if(!i.isBuiltOnOrMortgaged()) {
                Client.getRouter().send(PacketType.AUCTION_REQUEST, i);
            }
        });

        getChildren().add(new ThinPlotUI(i));
        getChildren().add(mortgageButton);
        getChildren().add(auctionButton);

        HBox.setMargin(mortgageButton, MARGIN_10);
        load(i);
    }

    public void setButtonsDisable(boolean b) {
        mortgageButton.setDisable(b);

        if(!plot.isMortgaged()) {
            auctionButton.setDisable(b);
        }
    }

    public void load(PropertyPlot i) {
        this.plot = i;

        if(i.isMortgaged()) {
            mortgageButton.setText("Unmortgage - £" + i.getUnmortgageCost() + ".00");
            auctionButton.setDisable(true);
        } else {
            mortgageButton.setText("Mortgage - £" + i.getValue() / 2 + ".00");
            auctionButton.setDisable(false);
        }
    }
}
