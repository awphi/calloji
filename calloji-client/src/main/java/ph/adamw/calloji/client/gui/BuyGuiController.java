package ph.adamw.calloji.client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.monopoly.PlotUI;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

import java.io.IOException;

public class BuyGuiController {
    private static Stage stage;
    private static PropertyPlot plot;

    @FXML
    private Label title;

    @FXML
    private VBox vbox;

    @FXML
    private void initialize() {
        title.setText("You have been offered the deed to:\n" + plot.getName());
        final PlotUI plotUI = new PlotUI();
        plotUI.setMinHeight(75);
        plotUI.setMinWidth(60);
        plotUI.load(plot);
        vbox.getChildren().add(plotUI);
        stage.setOnCloseRequest(event -> onAuctionPressed(null));
    }

    public static void open(Window window, PropertyPlot plot) {
        BuyGuiController.plot = plot;

        try {
            stage = new Stage();
            stage.setTitle("Purchase Deed [awphi]");
            GuiUtils.openOwnedWindow(window, "/fxml/buy.fxml", stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAuctionPressed(ActionEvent actionEvent) {
        Client.getRouter().send(PacketType.AUCTION_REQUEST, plot);
        stage.close();
    }

    @FXML
    private void onPurchasePressed(ActionEvent actionEvent) {
        Client.getRouter().send(PacketType.PLOT_PURCHASED, plot);
        stage.close();
    }
}
