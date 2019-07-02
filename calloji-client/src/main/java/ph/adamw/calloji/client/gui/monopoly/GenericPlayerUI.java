package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.client.gui.NullSelectionModel;
import ph.adamw.calloji.packet.data.Player;
import ph.adamw.calloji.packet.data.PlayerUpdate;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

@Slf4j
public class GenericPlayerUI extends VBox {
    public static final int GAME_PIECE_SIZE = 40;
    private static final Insets INSETS_10_RIGHT = new Insets(0, 10, 0, 0);
    private static final Insets PADDING = new Insets(10, 10, 10, 10);

    @Getter
    private final long pid;

    @Getter
    private PlayerUpdate lastUpdate;

    private GamePieceUI gamePieceOnBoard = null;

    private final ImageView gamePieceImage;
    private final Label nickname;
    private final Label money;

    private final ListView<ThinPlotUI> ownedPlots;

    public GenericPlayerUI(PlayerUpdate update, BoardUI boardUI) {
        setPadding(PADDING);
        final HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        setMaxHeight(USE_COMPUTED_SIZE);

        this.pid = update.getId();
        this.lastUpdate = update;

        final Player player = update.getPlayer();

        if(boardUI != null) {
            this.gamePieceOnBoard = new GamePieceUI(player.getGamePiece(), player.getBoardPosition(), boardUI, this);
        }

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
        getChildren().add(ownedPlots);

        reload(update);
    }

    public void deleteGamePiece() {
        gamePieceOnBoard.delete();
    }

    public void reload(PlayerUpdate update) {
        final Player player = update.getPlayer();

        if(gamePieceOnBoard != null) {
            gamePieceOnBoard.moveTo(player.getBoardPosition());
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

        money.setText("£" + player.getBalance());

        gamePieceImage.setImage(GuiUtils.getGamePieceImage(player.getGamePiece()));

        ownedPlots.getItems().clear();
        for(PropertyPlot i : update.getPlayer().getOwnedPlots(Client.getGui().getBoardCache())) {
            ownedPlots.getItems().add(new ThinPlotUI(i));
        }

        lastUpdate = update;
    }
}
