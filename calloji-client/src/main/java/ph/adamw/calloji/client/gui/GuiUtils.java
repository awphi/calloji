package ph.adamw.calloji.client.gui;

import com.google.common.collect.ImmutableMap;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.data.GamePiece;
import ph.adamw.calloji.packet.data.MessageType;
import ph.adamw.calloji.packet.data.plot.PlotType;

import java.io.IOException;
import java.io.InputStream;

public class GuiUtils {
    public static Image getGamePieceImage(GamePiece piece) {
        return new Image(getGamePieceImageStream(piece));
    }

    public static InputStream getGamePieceImageStream(GamePiece piece) {
        return GuiUtils.class.getResourceAsStream("/monopoly/" + piece.getImageRef() + ".png");
    }

    public static String formatSecondMinutes(int seconds) {
        return (Integer.toString(seconds / 60).length() == 1 ? "0" : "")
                + seconds / 60 + ":"
                + (Integer.toString(seconds % 60).length() == 1 ? "0" : "")
                + seconds % 60;
    }

    public static Label buildStyledLabel(String x, String... classes) {
        final Label text = new Label(x);
        text.getStyleClass().addAll(classes);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setWrapText(true);
        return text;
    }

    public static FXMLLoader loadFXML(String res) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource(res));
        fxmlLoader.load();
        return fxmlLoader;
    }

    public static <T> T openOwnedWindow(Window window, String res, Stage stage) throws IOException {
        final FXMLLoader fxmlLoader = loadFXML(res);

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(window);
        stage.setScene(new Scene(fxmlLoader.getRoot()));
        stage.setResizable(false);
        stage.show();

        return fxmlLoader.getController();
    }

    public static void setRegionSize(Region region, double width, double height) {
        region.setMinWidth(width);
        region.setMaxWidth(width);
        region.setMinHeight(height);
        region.setMaxHeight(height);
    }

    public static Thread startRunner(String name, Runnable runnable, long millis) {
        final Thread t = new Thread(() -> {
            while(true) {
                Platform.runLater(runnable);

                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ignored) {}
            }
        });

        t.setName(name);
        t.start();
        return t;
    }

    public static final ImmutableMap<PlotType, Color> PLOT_COLOR_MAP = new ImmutableMap.Builder<PlotType, Color>()
            .put(PlotType.RED, Color.RED)
            .put(PlotType.BROWN, Color.BROWN)
            .put(PlotType.LIGHT_BLUE, Color.LIGHTBLUE)
            .put(PlotType.PINK, Color.PINK)
            .put(PlotType.ORANGE, Color.ORANGE)
            .put(PlotType.YELLOW, Color.YELLOW)
            .put(PlotType.GREEN, Color.GREEN)
            .put(PlotType.BLUE, Color.ROYALBLUE)
            .build();

    public static final ImmutableMap<MessageType, Color> MESAGE_COLOR_MAP = new ImmutableMap.Builder<MessageType, Color>()
            .put(MessageType.CHAT, Color.BLACK)
            .put(MessageType.ADMIN, Color.DARKBLUE)
            .put(MessageType.WARNING, Color.RED)
            .put(MessageType.SYSTEM, Color.GRAY)
            .build();
}
