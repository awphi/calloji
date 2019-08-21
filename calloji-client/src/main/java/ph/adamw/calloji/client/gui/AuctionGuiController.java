package ph.adamw.calloji.client.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.monopoly.PlotUI;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.BidUpdate;
import ph.adamw.calloji.packet.data.MessageType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.util.GameConstants;

import java.io.IOException;

@Log4j2
public class AuctionGuiController {
    private static Stage stage;
    private static PropertyPlot plot;
    private static int plotWidth;
    private static int plotHeight;

    @Getter
    private static AuctionGuiController activeGui;

    @FXML
    private Label auctionTimer;

    @FXML
    private Label title;

    private int time = GameConstants.AUCTION_TIME;

    private Thread timer;

    @FXML
    private VBox vbox;

    @FXML
    private TextField bidTextField;

    @FXML
    private Label topBidderLabel;

    private BidUpdate lastUpdate;

    @FXML
    private void initialize() {
        title.setText("Auction For:\n" + plot.getName());
        final PlotUI plotUI = new PlotUI();
        plotUI.load(plot);
        GuiUtils.setRegionSize(plotUI, plotWidth, plotHeight);
        vbox.getChildren().add(plotUI);

        timer = GuiUtils.startRunner("ATimer", this::decrementTimer, 1000);
        stage.setOnCloseRequest(event -> timer.interrupt());

        bidTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            if (newText.matches("-?([1-9][0-9]*)?")) {
                return change;
            }

            return null;
        }));
    }

    private void decrementTimer() {
        if(time > 0) {
            time --;
            auctionTimer.setText(GuiUtils.formatSecondMinutes(time));

            if(time == 0) {
                String msg = "Nobody bid so the property remains on the market!";
                if(lastUpdate != null) {
                    msg = lastUpdate.getWinnerNick() + " won with a bid of £" + lastUpdate.getAmount() + ".00!";
                }

                Client.getGui().displayChatMessage(MessageType.SYSTEM, "The auction is over. " + msg);
                stage.close();
            }
        }
    }

    public static void open(Window window, PropertyPlot plot) throws IOException {
        AuctionGuiController.plot = plot;
        AuctionGuiController.plotWidth = Client.getGui().getBoardUI().getPlotWidth();
        AuctionGuiController.plotHeight = Client.getGui().getBoardUI().getPlotHeight();

        stage = new Stage();
        stage.setTitle("Auction [awphi]");
        activeGui = GuiUtils.openOwnedWindow(window, "/fxml/auction.fxml", stage);
    }

    public void onBidUpdate(BidUpdate bidUpdate) {
        this.lastUpdate = bidUpdate;
        Platform.runLater(() -> topBidderLabel.setText("Top Bidder: " + bidUpdate.getWinnerNick() + " - £" + bidUpdate.getAmount() + ".00"));
    }


    @FXML
    private void onBidPressed(ActionEvent actionEvent) {
        Client.getRouter().send(PacketType.AUCTION_BID, Integer.parseInt(bidTextField.getText()));
        bidTextField.clear();
    }

    @FXML
    private void onBidFieldKeyUp(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) {
            onBidPressed(null);
        }
    }

}
