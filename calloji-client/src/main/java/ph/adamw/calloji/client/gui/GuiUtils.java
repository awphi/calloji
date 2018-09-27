package ph.adamw.calloji.client.gui;

import javafx.scene.image.Image;
import ph.adamw.calloji.data.GamePiece;

import java.io.InputStream;

public class GuiUtils {
    public static Image getGamePieceImage(GamePiece piece) {
        return new Image(getGamePieceImageStream(piece));
    }

    public static InputStream getGamePieceImageStream(GamePiece piece) {
        return GuiUtils.class.getResourceAsStream("/monopoly/" + piece.getImageRef() + ".png");
    }
}
