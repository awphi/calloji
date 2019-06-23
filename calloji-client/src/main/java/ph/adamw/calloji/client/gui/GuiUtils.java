package ph.adamw.calloji.client.gui;

import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import ph.adamw.calloji.packet.data.GamePiece;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Pattern;

public class GuiUtils {
    public static Image getGamePieceImage(GamePiece piece) {
        return new Image(getGamePieceImageStream(piece));
    }

    public static InputStream getGamePieceImageStream(GamePiece piece) {
        return GuiUtils.class.getResourceAsStream("/monopoly/" + piece.getImageRef() + ".png");
    }
}
