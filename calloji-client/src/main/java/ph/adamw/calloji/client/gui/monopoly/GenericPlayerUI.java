package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.packet.data.Player;
import ph.adamw.calloji.packet.data.PlayerUpdate;

@Slf4j
public class GenericPlayerUI extends HBox {
    public static final int GAME_PIECE_SIZE = 40;
    private static Insets INSETS_10_RIGHT = new Insets(0, 10, 0, 0);

    @Getter
    private final long pid;

    @Getter
    private PlayerUpdate lastUpdate;

    private final GamePieceUI gamePieceOnBoard;

    private final ImageView gamePieceImage;
    private final Text nickname;
    private final Text money;

    public GenericPlayerUI(PlayerUpdate update, BoardUI boardUI) {
        this.pid = update.getId();
        this.lastUpdate = update;

        final Player player = update.getPlayer();

        this.gamePieceOnBoard = new GamePieceUI(player.getGamePiece(), player.getBoardPosition(), boardUI, this);

        setAlignment(Pos.CENTER_LEFT);

        gamePieceImage = new ImageView();
        gamePieceImage.maxHeight(GAME_PIECE_SIZE);
        gamePieceImage.maxWidth(GAME_PIECE_SIZE);
        gamePieceImage.setFitHeight(GAME_PIECE_SIZE);
        gamePieceImage.setFitWidth(GAME_PIECE_SIZE);
        setMargin(gamePieceImage, INSETS_10_RIGHT);
        getChildren().add(gamePieceImage);

        nickname = new Text();
        setMargin(nickname, INSETS_10_RIGHT);
        getChildren().add(nickname);

        money = new Text();
        money.setFill(Color.GREEN);
        getChildren().add(money);

        reload(update);
    }

    public void deleteGamePiece() {
        gamePieceOnBoard.delete();
    }

    public void reload(PlayerUpdate update) {
        final Player player = update.getPlayer();

        gamePieceOnBoard.moveTo(player.getBoardPosition());

        if(Client.getRouter().getPid() == update.getId()) {
            nickname.setFill(Color.ROYALBLUE);
            nickname.setText(update.getNick() + " (Me)");
        } else {
            nickname.setFill(Color.ORANGERED);
            nickname.setText(update.getNick());
        }

        if(player.isBankrupt()) {
            money.setFill(Color.RED);
            // TODO grey em out
        }

        money.setText("£" + player.getBalance() + ".00");

        gamePieceImage.setImage(GuiUtils.getGamePieceImage(player.getGamePiece()));

        // TODO use last player to do animations for gaining/losing money etc.
        lastUpdate = update;
    }
}
