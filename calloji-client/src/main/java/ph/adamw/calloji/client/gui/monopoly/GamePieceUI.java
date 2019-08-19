package ph.adamw.calloji.client.gui.monopoly;

import javafx.animation.PathTransition;
import javafx.animation.Transition;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.packet.data.GamePiece;
import ph.adamw.calloji.packet.data.MoveType;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class GamePieceUI extends ImageView {
    private final BoardUI boardUI;
    private int currentPos = 0;

    private final List<AnimationRequest> animationQueue = new ArrayList<>();

    @Getter
    private boolean isAnimated = false;

    private Transition currentAnimation;

    public GamePieceUI(GamePiece piece, BoardUI boardUI) {
        super(GuiUtils.getGamePieceImage(piece));
        this.boardUI = boardUI;

        StackPane.setAlignment(this, Pos.TOP_LEFT);
        setFitHeight(GenericPlayerUI.GAME_PIECE_SIZE);
        setFitWidth(GenericPlayerUI.GAME_PIECE_SIZE);
    }

    public void moveTo(final int goal, final MoveType moveType) {
        if(moveType == MoveType.NONE) {
            return;
        }

        if(isAnimated) {
            animationQueue.add(new AnimationRequest(goal, moveType));
            return;
        }

        isAnimated = true;

        final Path path = new Path();

        final Point2D start = boardUI.getPointFromBoardPos(currentPos);
        final MoveTo moveTo = new MoveTo(start.getX(), start.getY());
        path.getElements().add(0, moveTo);

        int goalPos = goal;
        int cornerPos = currentPos;

        if(moveType == MoveType.FORWARD && goal <= currentPos) {
            goalPos += 40;
        }

        final int corners = Math.abs(multiplesInRange(currentPos, goalPos, 10));

        for(int i = 0; i < corners; i ++) {
            cornerPos = getNextCorner(cornerPos, moveType);
            final Point2D nextCorner = boardUI.getPointFromBoardPos(cornerPos);
            path.getElements().add(new LineTo(nextCorner.getX(), nextCorner.getY()));
        }

        final Point2D fin = boardUI.getPointFromBoardPos(goal);
        path.getElements().add(new LineTo(fin.getX(), fin.getY()));

        final PathTransition anim = new PathTransition(Duration.seconds(Math.abs(goalPos - currentPos) * 0.125d), path, this);
        anim.setOnFinished(event -> {
            toFront();

            isAnimated = false;

            if(!animationQueue.isEmpty()) {
                final AnimationRequest req = animationQueue.remove(0);
                moveTo(req.getBoardPos(), req.getMoveType());
            }
        });

        currentPos = goal;
        currentAnimation = anim;
        anim.playFromStart();
    }

    private static int multiplesInRange(int from, int to, int multiple) {
        return (to / multiple) - (from / multiple);
    }

    private static int getNextCorner(double a, final MoveType type) {
        if (a % 10 == 0) {
            final int nudge = type == MoveType.FORWARD ? 10 : -10;

            return Math.floorMod((int) a + nudge, 40);
        } else {
            final double num = type == MoveType.FORWARD ? Math.ceil(a / 10d) * 10d : Math.floor(a / 10d) * 10d;
            return Math.floorMod((int) num, 40);
        }
    }

    public void cancelAnimation() {
        if(!animationQueue.isEmpty()) {
            animationQueue.clear();
        }

        if(currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }

        isAnimated = false;
    }

    public void reposition() {
        final Point2D point = boardUI.getPointFromBoardPos(currentPos);
        setTranslateX(point.getX() - getFitWidth() / 2);
        setTranslateY(point.getY() - getFitHeight() / 2);
    }

    @AllArgsConstructor
    @Getter
    private static class AnimationRequest {
        private final int boardPos;
        private final MoveType moveType;
    }
}