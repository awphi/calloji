package ph.adamw.calloji.client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.anim.ShakeTransition;

public class SplashController {
    @FXML
    private TextField ipField;

    @FXML
    private void onConnectButtonPressed(ActionEvent actionEvent) {
        final String[] split = ipField.getText().split(":");

        if(split.length < 2 || !split[1].matches("[0-9]+") || !Client.attemptConnect(split[0], Integer.parseInt(split[1]))) {
            final ShakeTransition shake = new ShakeTransition(ipField, null);
            shake.playFromStart();
        } else {
            Client.closeSplash();
        }
    }
}
