package ph.adamw.calloji.client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.anim.ShakeTransition;

import java.io.IOException;

public class SplashController {
    @FXML
    private TextField ipField;

    private static Stage splashStage;

    public static void openSplash(Window window) {
        if(splashStage != null) {
            return;
        }

        final FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("/fxml/splash.fxml"));
        try {
            splashStage = new Stage();
            splashStage.setTitle("New Calloji connection [awphi]");
            splashStage.initModality(Modality.WINDOW_MODAL);
            splashStage.initOwner(window);
            splashStage.setScene(new Scene(fxmlLoader.load()));
            splashStage.setResizable(false);
            splashStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void closeSplash() {
        splashStage.close();
        splashStage = null;
    }


    @FXML
    private void onConnectButtonPressed(ActionEvent actionEvent) {
        final String[] split = ipField.getText().split(":");

        if(split.length < 2 || !split[1].matches("[0-9]+") || !Client.attemptConnect(split[0], Integer.parseInt(split[1]))) {
            final ShakeTransition shake = new ShakeTransition(ipField, null);
            shake.playFromStart();
        } else {
            SplashController.closeSplash();
        }
    }

    @FXML
    private void onIpFieldEnter(ActionEvent actionEvent) {
        onConnectButtonPressed(actionEvent);
    }
}
