package ph.adamw.calloji.client.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.anim.ShakeTransition;

import java.io.IOException;

public class SplashController {
    @FXML
    private TextField ipField;

    private static Stage splashStage;

    public static void open(Window window) {
        if(splashStage != null) {
            return;
        }

        try {
            splashStage = new Stage();
            splashStage.setTitle("New Calloji connection [awphi]");
            splashStage.setOnCloseRequest(event -> splashStage = null);
            GuiUtils.openOwnedWindow(window, "/fxml/splash.fxml", splashStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void onConnectButtonPressed(ActionEvent actionEvent) {
        final String[] split = ipField.getText().split(":");

        if(split.length < 2 || !split[1].matches("[0-9]+") || !Client.attemptConnect(split[0], Integer.parseInt(split[1]))) {
            final ShakeTransition shake = new ShakeTransition(ipField, null);
            shake.playFromStart();
        } else {
            splashStage.close();
        }
    }

    @FXML
    private void onIpKeyUp(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            onConnectButtonPressed(null);
        }
    }
}
