package ph.adamw.calloji.client.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.StringUtil;
import ph.adamw.calloji.client.gui.monopoly.PlotUI;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

import java.io.IOException;

public class AuctionGuiController {
    private static Stage stage;
    private static PropertyPlot plot;

    @FXML
    private Text auctionTimer;

    @FXML
    private Label title;

    private int time = 30;

    private Thread timer;

    @FXML
    private VBox vbox;

    @FXML
    private void initialize() {
        title.setText("Auction For:\n" + plot.getName());
        final PlotUI plotUI = new PlotUI(null);
        plotUI.load(plot);
        vbox.getChildren().add(plotUI);

        timer = GuiUtils.startRunner(this::decrementTimer, 1000);
        stage.setOnCloseRequest(event -> timer.interrupt());
    }

    private void decrementTimer() {
        if(time > 0) {
            time --;
            auctionTimer.setText(StringUtil.formatSecondMinutes(time));

            if(time == 0) {
                Client.printMessage(MessageType.SYSTEM, "Time's up!");
                stage.close();
            }
        }
    }

    public static void open(Window window, PropertyPlot plot) {
        AuctionGuiController.plot = plot;

        try {
            stage = new Stage();
            stage.setTitle("Auction [awphi]");
            GuiUtils.openOwnedWindow(window, "/fxml/auction.fxml", stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBidPressed(ActionEvent actionEvent) {
        //TODO submit a bid packet
    }
}
