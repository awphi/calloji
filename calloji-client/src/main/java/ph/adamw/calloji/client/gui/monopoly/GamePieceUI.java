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

@Log4j2
public class GamePieceUI extends ImageView {
    private final BoardUI boardUI;
    private int currentPos = 0;

    private static final double DEFAULT_OPACITY = 0.4;
    private static final double MOVING_OPACITY = 1;


    public GamePieceUI(GamePiece piece, BoardUI boardUI) {
        super(GuiUtils.getGamePieceImage(piece));
        this.boardUI = boardUI;

        setOpacity(DEFAULT_OPACITY);

        StackPane.setAlignment(this, Pos.TOP_LEFT);
        setFitHeight(GenericPlayerUI.GAME_PIECE_SIZE);
        setFitWidth(GenericPlayerUI.GAME_PIECE_SIZE);
    }

    public void moveTo(int pos) {
        if(pos == currentPos) {
            return;
        }

        final Path path = new Path();

        int c = getNextCorner(currentPos);
        final int to = getNextCorner(pos);

        // While we're not the same line
        while(c != to) {
            final Point2D nextCorner = boardUI.getPointFromBoardPos(c);
            path.getElements().add(new LineTo(nextCorner.getX(), nextCorner.getY()));
            c = getNextCorner(c);
        }

        final Point2D start = boardUI.getPointFromBoardPos(currentPos);
        final MoveTo moveTo = new MoveTo(start.getX(), start.getY());
        path.getElements().add(0, moveTo);

        final Point2D goal = boardUI.getPointFromBoardPos(pos);
        path.getElements().add(new LineTo(goal.getX(), goal.getY()));

        log.debug(path.getElements());
        final PathTransition pathTransition = new PathTransition(Duration.seconds((path.getElements().size() - 1) * 2), path, this);
        pathTransition.setOnFinished(event -> {
            setOpacity(DEFAULT_OPACITY);
            toFront();
        });
        setOpacity(MOVING_OPACITY);
        pathTransition.playFromStart();

        currentPos = pos;
    }

    private int getNextCorner(double a) {
        if(a % 10 == 0) {
            return (int) ((a + 10) % 40);
        }

        return ((int) (Math.ceil(a / 10d) * 10d)) % 40;
    }
}