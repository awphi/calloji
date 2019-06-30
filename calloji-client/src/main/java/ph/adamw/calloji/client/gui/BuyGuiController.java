package ph.adamw.calloji.client.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.monopoly.BoardUI;
import ph.adamw.calloji.client.gui.monopoly.PlotUI;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.Plot;
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
        title.setText("You have been offered the deed to: " + plot.getName());
        final PlotUI plotUI = new PlotUI(null);
        plotUI.load(plot);
        plotUI.setMinHeight(BoardUI.HEIGHT);
        vbox.getChildren().add(plotUI);
        stage.setOnCloseRequest(event -> onAuctionPressed(null));
    }

    public static void open(Window window, PropertyPlot plot) {
        final FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("/fxml/buy.fxml"));
        BuyGuiController.plot = plot;

        try {
            stage = new Stage();
            stage.setTitle("Purchase Deed [awphi]");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(window);
            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAuctionPressed(ActionEvent actionEvent) {
        //TODO auction packets
        stage.close();
    }

    @FXML
    private void onPurchasePressed(ActionEvent actionEvent) {
        Client.getRouter().send(PacketType.PLOT_PURCHASED, plot);
        stage.close();
    }
}
