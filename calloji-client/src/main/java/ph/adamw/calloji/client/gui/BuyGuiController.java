package ph.adamw.calloji.client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.monopoly.PlotUI;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

import java.io.IOException;

@Log4j2
public class BuyGuiController {
    private static Stage stage;
    private static PropertyPlot plot;
    private static int plotWidth;
    private static int plotHeight;

    @FXML
    private Label title;

    @FXML
    private VBox vbox;

    @FXML
    private void initialize() {
        title.setText("You have been offered the deed to:\n" + plot.getName());
        final PlotUI plotUI = new PlotUI();
        plotUI.load(plot);
        GuiUtils.setRegionSize(plotUI, plotWidth, plotHeight);
        vbox.getChildren().add(plotUI);
        stage.setOnCloseRequest(event -> onAuctionPressed(null));
    }

    public static void open(Window window, PropertyPlot plot) throws IOException {
        BuyGuiController.plot = plot;
        BuyGuiController.plotWidth = Client.getGui().getBoardUI().getPlotWidth();
        BuyGuiController.plotHeight = Client.getGui().getBoardUI().getPlotHeight();

        stage = new Stage();
        stage.setTitle("Purchase Deed [awphi]");
        GuiUtils.openOwnedWindow(window, "/fxml/buy.fxml", stage);
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
