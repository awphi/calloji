package ph.adamw.calloji.client.gui.monopoly;

import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.packet.data.GamePiece;
import ph.adamw.calloji.packet.data.MoveType;

@Log4j2
public class GamePieceUI extends ImageView {
    private final BoardUI boardUI;
    private int currentPos = 0;


    public GamePieceUI(GamePiece piece, BoardUI boardUI) {
        super(GuiUtils.getGamePieceImage(piece));
        this.boardUI = boardUI;

        StackPane.setAlignment(this, Pos.TOP_LEFT);
        setFitHeight(GenericPlayerUI.GAME_PIECE_SIZE);
        setFitWidth(GenericPlayerUI.GAME_PIECE_SIZE);
    }

    public void moveTo(int pos, final MoveType moveType) {
        if(moveType == MoveType.NONE) {
            return;
        }

        final Path path = new Path();

        int c = getNextCorner(currentPos, moveType);
        final int to = getNextCorner(pos, moveType);

        // While we're not the same line
        int counter = 0;
        while(c != to || (pos == currentPos && counter < 3)) {
            final Point2D nextCorner = boardUI.getPointFromBoardPos(c);
            path.getElements().add(new LineTo(nextCorner.getX(), nextCorner.getY()));
            c = getNextCorner(c, moveType);
            counter ++;
        }

        final Point2D start = boardUI.getPointFromBoardPos(currentPos);
        final MoveTo moveTo = new MoveTo(start.getX(), start.getY());
        path.getElements().add(0, moveTo);

        final Point2D goal = boardUI.getPointFromBoardPos(pos);
        path.getElements().add(new LineTo(goal.getX(), goal.getY()));

        log.debug(path.getElements());
        final PathTransition pathTransition = new PathTransition(Duration.seconds((path.getElements().size() - 1) * 2), path, this);
        pathTransition.setOnFinished(event -> toFront());
        pathTransition.playFromStart();

        currentPos = pos;
    }

    private static int getNextCorner(double a, final MoveType type) {
        if (a % 10 == 0) {
            final int nudge = type == MoveType.FORWARD ? 10 : -10;

            return (int) ((a + nudge) % 40);
        } else {
            final double num = type == MoveType.FORWARD ? Math.ceil(a / 10d) * 10d : Math.floor(a / 10d) * 10d;
            return ((int) num) % 40;
        }
    }
}