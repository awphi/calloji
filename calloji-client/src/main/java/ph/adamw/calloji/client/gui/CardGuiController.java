package ph.adamw.calloji.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.data.CardUpdate;

import java.io.IOException;

@Log4j2
public class CardGuiController {
    private static CardUpdate update;
    private static Stage stage;

    @FXML
    private Label deckLabel;
    @FXML
    private Label text;

    @FXML
    public void initialize() {
        deckLabel.setText(update.getDeck());
        text.setText(update.getText());
    }

    public static void open(Window window, CardUpdate update) {
        CardGuiController.update = update;

        try {
            stage = new Stage();
            stage.setTitle(update.getDeck() + " Card [awphi]");
            GuiUtils.openOwnedWindow(window, "/fxml/card.fxml", stage);
        } catch (IOException e) {
            log.trace(e);
        }
    }
}
