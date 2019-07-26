package ph.adamw.calloji.client.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.data.GamePiece;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class GuiUtils {
    public static Image getGamePieceImage(GamePiece piece) {
        return new Image(getGamePieceImageStream(piece));
    }

    public static InputStream getGamePieceImageStream(GamePiece piece) {
        return GuiUtils.class.getResourceAsStream("/monopoly/" + piece.getImageRef() + ".png");
    }

    public static <T> T openOwnedWindow(Window window, String res, Stage stage) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource(res));
        final Parent p = fxmlLoader.load();
        final T cache = fxmlLoader.getController();

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(window);
        stage.setScene(new Scene(p));
        stage.setResizable(false);
        stage.show();
        return cache;
    }

    public static Thread startRunner(Runnable runnable, long millis) {
        final Thread t = new Thread(() -> {
            while(true) {
                Platform.runLater(runnable);

                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ignored) {}
            }
        });

        t.start();
        return t;
    }
}
