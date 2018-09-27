package ph.adamw.calloji.client.gui.monopoly;

import javafx.event.EventHandler;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.data.GamePiece;

@Slf4j
public class GamePieceUI extends ImageView {
    private final BoardUI boardUI;
    private int boardPosition = -1;
    private final GenericPlayerUI owner;

    private static final ColorAdjust colorAdjust = new ColorAdjust();

    public GamePieceUI(GamePiece piece, int boardPosition, BoardUI boardUI, GenericPlayerUI owner) {
        super(GuiUtils.getGamePieceImage(piece));
        this.boardUI = boardUI;
        this.owner = owner;

        colorAdjust.setBrightness(0.2);

        moveTo(boardPosition);

        setFitHeight(GenericPlayerUI.GAME_PIECE_SIZE);
        setFitWidth(GenericPlayerUI.GAME_PIECE_SIZE);

        setOnMouseEntered(event -> setEffect(colorAdjust));

        setOnMouseExited(event -> setEffect(null));

        setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                Client.getGui().focusGenericPlayer(owner);
            }
        });
    }

    public void moveTo(int pos) {
        if(boardPosition != -1) {
            boardUI.getRenderedPlot(boardPosition).removeCentre(this);
        }

        boardUI.getRenderedPlot(pos).addCentre(this);
        boardPosition = pos;
    }

    public void delete() {
        boardUI.getRenderedPlot(boardPosition).removeCentre(this);
    }
}