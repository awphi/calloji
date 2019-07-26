package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

public class ManagedAssetUI extends HBox {
    private final static Insets MARGIN_10 = new Insets(0, 10, 0, 10);

    public ManagedAssetUI(PropertyPlot i) {
        final Button mortgageButton = new Button("Mortgage - £" + i.getValue() / 2 + ".00");
        final Button auctionButton = new Button("Auction");

        setAlignment(Pos.CENTER);

        mortgageButton.setOnAction(event -> {
            //TODO send mortgage request packet
        });

        auctionButton.setOnAction(event -> {
            //TODO send auction request packet
        });

        getChildren().add(new ThinPlotUI(i));
        getChildren().add(mortgageButton);
        getChildren().add(auctionButton);

        HBox.setMargin(mortgageButton, MARGIN_10);
    }
}
