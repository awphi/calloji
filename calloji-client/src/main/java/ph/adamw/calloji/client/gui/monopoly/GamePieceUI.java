package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.packet.data.GamePiece;

@Log4j2
public class GamePieceUI extends ImageView {
    private final BoardUI boardUI;

    private static final ColorAdjust colorAdjust = new ColorAdjust();

    public GamePieceUI(GamePiece piece, int boardPosition, BoardUI boardUI) {
        super(GuiUtils.getGamePieceImage(piece));
        this.boardUI = boardUI;

        colorAdjust.setBrightness(0.2);

        StackPane.setAlignment(this, Pos.TOP_LEFT);
        setFitHeight(GenericPlayerUI.GAME_PIECE_SIZE);
        setFitWidth(GenericPlayerUI.GAME_PIECE_SIZE);

        setOnMouseEntered(event -> setEffect(colorAdjust));
        setOnMouseExited(event -> setEffect(null));

        moveTo(boardPosition);

    }

    //TODO path transition
    public void moveTo(int pos) {
        final Point2D point2D = boardUI.getPointFromBoardPos(pos);
        setTranslateX(point2D.getX() - (getFitWidth() / 2));
        setTranslateY(point2D.getY() - (getFitHeight() / 2));
        toFront();
    }
}