package ph.adamw.calloji.client.gui;

import com.google.gson.JsonPrimitive;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.anim.ShakeTransition;
import ph.adamw.calloji.packet.PacketType;

import java.io.IOException;

@Log4j2
public class SplashController {
    @FXML
    private TextField ipField;

    private static Stage splashStage;
    @FXML

    private TextField nickField;

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
            log.trace(e);
        }
    }


    @FXML
    private void onConnectButtonPressed(ActionEvent actionEvent) {
        final String[] split = ipField.getText().split(":");

        if(nickField.getText().isEmpty() || ipField.getText().isEmpty() || split.length < 2 || !split[1].matches("[0-9]+") || !Client.getRouter().attemptConnect(split[0], Integer.parseInt(split[1]))) {
            final ShakeTransition shake = new ShakeTransition(ipField, null);
            shake.playFromStart();
        } else {
            splashStage.close();
            splashStage = null;
            Client.getRouter().send(PacketType.NICK_EDIT, new JsonPrimitive(nickField.getText()));
        }
    }

    @FXML
    private void onIpKeyUp(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            if(nickField.getText().isEmpty()) {
                nickField.requestFocus();
            } else {
                onConnectButtonPressed(null);
            }
        }
    }

    @FXML
    private void onNickKeyUp(KeyEvent keyEvent) {
        if(keyEvent.getCode().equals(KeyCode.ENTER)) {
            onConnectButtonPressed(null);
        }
    }
}
