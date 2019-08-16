package ph.adamw.calloji.client.gui.monopoly;

import com.sun.xml.internal.bind.v2.model.core.ID;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.client.gui.NullSelectionModel;
import ph.adamw.calloji.packet.data.Player;
import ph.adamw.calloji.packet.data.PlayerUpdate;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

@Log4j2
public class GenericPlayerUI extends VBox {
    public static final int GAME_PIECE_SIZE = 40;
    public static final double IDLE_OPACITY = 0.3;

    private static final Insets INSETS_10_RIGHT = new Insets(0, 10, 0, 0);
    private static final Insets PADDING = new Insets(10, 10, 10, 10);

    @Getter
    private final long pid;

    @Getter
    private PlayerUpdate lastUpdate;

    @Getter
    private final GamePieceUI gamePieceOnBoard;

    private final ImageView gamePieceImage;
    private final Label nickname;
    private final Label money;

    private final ListView<ThinPlotUI> ownedPlots;

    public GenericPlayerUI(PlayerUpdate update, BoardUI boardUI, StackPane stackPane) {
        setPadding(PADDING);
        final HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        setMaxHeight(USE_COMPUTED_SIZE);

        this.pid = update.getId();
        this.lastUpdate = update;

        final Player player = update.getPlayer();

        gamePieceOnBoard = new GamePieceUI(player.getGamePiece(), boardUI);
        gamePieceOnBoard.setOpacity(IDLE_OPACITY);
        stackPane.getChildren().add(gamePieceOnBoard);

        final Point2D go = boardUI.getPointFromBoardPos(0);
        gamePieceOnBoard.setTranslateX(go.getX() - (gamePieceOnBoard.getFitWidth() / 2));
        gamePieceOnBoard.setTranslateY(go.getY() - (gamePieceOnBoard.getFitHeight() / 2));

        gamePieceImage = new ImageView();
        gamePieceImage.maxHeight(GAME_PIECE_SIZE);
        gamePieceImage.maxWidth(GAME_PIECE_SIZE);
        gamePieceImage.setFitHeight(GAME_PIECE_SIZE);
        gamePieceImage.setFitWidth(GAME_PIECE_SIZE);
        HBox.setMargin(gamePieceImage, INSETS_10_RIGHT);
        hbox.getChildren().add(gamePieceImage);

        nickname = new Label();
        HBox.setMargin(nickname, INSETS_10_RIGHT);
        hbox.getChildren().add(nickname);

        money = new Label();
        money.setTextFill(Color.GREEN);
        hbox.getChildren().add(money);

        getChildren().add(hbox);

        final Label plots = new Label();
        plots.setText("Owned Assets:");
        plots.getStyleClass().add("bold");
        getChildren().add(plots);

        ownedPlots = new ListView<>();
        ownedPlots.setSelectionModel(new NullSelectionModel<>());
        ownedPlots.setMinHeight(60);
        ownedPlots.setMaxHeight(60);
        ownedPlots.setPlaceholder(new Label("No owned properties."));
        getChildren().add(ownedPlots);

        reload(update);
    }

    public void reload(PlayerUpdate update) {
        final Player player = update.getPlayer();

        if(gamePieceOnBoard != null) {
            gamePieceOnBoard.moveTo(player.getBoardPosition(), player.getLastMoveType());
        }

        if(Client.getRouter().getPid() == update.getId()) {
            nickname.setTextFill(Color.ROYALBLUE);
            nickname.setText(update.getNick() + " (Me)");
        } else {
            nickname.setTextFill(Color.ORANGERED);
            nickname.setText(update.getNick());
        }

        if(player.isBankrupt()) {
            money.setTextFill(Color.RED);
            this.setDisable(true);
        }

        money.setText("£" + player.getBalance() + ".00");

        gamePieceImage.setImage(GuiUtils.getGamePieceImage(player.getGamePiece()));

        ownedPlots.getItems().clear();

        if(Client.getCache().getBoard() != null) {
            for (PropertyPlot i : Client.getCache().getOwnedPlots(update.getPlayer())) {
                ownedPlots.getItems().add(new ThinPlotUI(i));
            }
        }

        lastUpdate = update;
    }
}
