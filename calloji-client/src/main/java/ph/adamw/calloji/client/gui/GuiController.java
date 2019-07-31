package ph.adamw.calloji.client.gui;

import com.google.gson.JsonPrimitive;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.StringUtil;
import ph.adamw.calloji.client.gui.monopoly.BoardUI;
import ph.adamw.calloji.client.gui.monopoly.GenericPlayerUI;
import ph.adamw.calloji.client.gui.monopoly.ManagedAssetUI;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.*;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Slf4j
public class GuiController {
	@FXML
	private TextField chatTextField;

	@FXML
	private ListView<Label> chatListView;

	@FXML
	@Getter
	private Menu nicknameMenu;

	@FXML
	private BorderPane mainBorderPane;

	private final BoardUI boardUI = new BoardUI();

	private final ListView<GenericPlayerUI> playerListView = new ListView<>();

	@FXML
	private BorderPane playersBorderPane;

	@FXML
	private TabPane rightTabPane;

	@FXML
	private Tab playersTab;

	@FXML
	private Button rollDiceButton;

	@FXML
	private Label turnTimer;

	private Integer turnTime = 0;

	@FXML
	private Label jailedLabel;

	@FXML
	private Label getOutOfJailsLabel;

	@FXML
	private Label balanceLabel;

	@FXML
	@Getter
	private MenuItem disconnectButton;

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("K:mm");

	@FXML
	private ListView<ManagedAssetUI> assetManagementListView;

	public void displayChatMessage(MessageType type, String txt) {
		final Label text = new Label("[" + DATE_FORMAT.format(new Date()) + "] " + txt);
		text.setTextFill(type.getColor());
		Platform.runLater(() -> {
			chatListView.getItems().add(text);
			chatListView.scrollTo(chatListView.getItems().size() - 1);
		});
	}

	@FXML
	public void initialize() {
		playerListView.setSelectionModel(new NullSelectionModel<>());
		chatListView.setSelectionModel(new NullSelectionModel<>());
		assetManagementListView.setSelectionModel(new NullSelectionModel<>());

		playersBorderPane.setCenter(playerListView);
		mainBorderPane.setCenter(boardUI);

		chatListView.setPlaceholder(new Label("Not connected to a game!"));
		playerListView.setPlaceholder(new Label("Not connected to a game!"));
		assetManagementListView.setPlaceholder(new Label("No assets owned!"));

		final Thread timer = GuiUtils.startRunner("Turn Timer Decrementer", this::decrementTurnTimer, 1000);

		Client.getStage().setOnCloseRequest(event -> {
			timer.interrupt();
			Platform.exit();
		});
	}

	private void decrementTurnTimer() {
		if(turnTime > 0) {
			turnTime --;
			turnTimer.setText(StringUtil.formatSecondMinutes(turnTime));

			if(turnTime == 0 && !rollDiceButton.isDisabled()) {
				displayChatMessage(MessageType.SYSTEM, "Time's up!");
				setActionsDisabled(true);
			}
		}
	}

	private ManagedAssetUI getManagedAsset(PropertyPlot plot) {
		for(ManagedAssetUI i : assetManagementListView.getItems()) {
			if(i.getPlot().equals(plot)) {
				return i;
			}
		}

		return null;
	}

	public void updateManagedAssets(Board board) {
		for(PropertyPlot i : Client.getCache().getCachedPlayer(Client.getRouter().getPid()).getPlayer().getOwnedPlots(board)) {
			ManagedAssetUI ui = getManagedAsset(i);

			if(ui == null) {
				ui = new ManagedAssetUI(i);
				assetManagementListView.getItems().add(ui);
			}

			ui.load(i);
		}
	}

	private void setActionsDisabled(boolean b) {
		for(ManagedAssetUI i : assetManagementListView.getItems()) {
			i.setButtonsDisable(b);
		}

		rollDiceButton.setDisable(b);
	}

	@FXML
	public void onChatSubmitted(ActionEvent actionEvent) {
		if(chatTextField.getText().isEmpty()) {
			return;
		}

		Client.getRouter().send(PacketType.CHAT_MESSAGE, new ChatMessage(MessageType.CHAT, chatTextField.getText(), ""));
		chatTextField.clear();
	}

	@FXML
	private void onChatKeyUp(KeyEvent keyEvent) {
		if(keyEvent.getCode() == KeyCode.ENTER) {
			onChatSubmitted(null);
		}
	}

	@FXML
	private void onEditNicknamePressed(ActionEvent actionEvent) {
		final TextInputDialog dialog = new TextInputDialog("");
		dialog.getDialogPane().getStyleClass().add("window");
		dialog.setTitle("Edit Nickname");
		dialog.setHeaderText("Enter a new nickname");
		dialog.setContentText("Please enter your desired nick:");

		final Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> {
			if(!name.isEmpty()) {
				Client.getRouter().send(PacketType.NICK_EDIT, new JsonPrimitive(name));
			}
		});
	}

	public void loadBoard(Board board) {
		boardUI.loadBoard(board);
	}

	public void unloadBoard() {
		boardUI.unload();
	}

	private boolean isPlayerUpdateFresh(PlayerUpdate update) {
		for(GenericPlayerUI i : playerListView.getItems()) {
			if(i.getPid() == update.getId()) {
				return false;
			}
		}

		return true;
	}

	private void reloadPlayer(PlayerUpdate update) {
		for(GenericPlayerUI i : playerListView.getItems()) {
			if(i.getPid() == update.getId()) {
				i.reload(update);
				break;
			}
		}
	}

    public void loadPlayer(PlayerUpdate update) {
		if(update.getId() == Client.getRouter().getPid()) {
			balanceLabel.setText("Balance: £" + update.getPlayer().getBalance());
			getOutOfJailsLabel.setText("Get Out of Jail Cards: " + update.getPlayer().getGetOutOfJails());
			jailedLabel.setText("Jailed: " + update.getPlayer().getJailed());
		}

		// If there's no player to reload then this is a new player and we must create props for it
		if(isPlayerUpdateFresh(update)) {
			final GenericPlayerUI gen = new GenericPlayerUI(update, boardUI);

			if(update.getId() == Client.getRouter().getPid()) {
				playerListView.getItems().add(0, gen);
			} else {
				playerListView.getItems().add(gen);
			}
		}

		reloadPlayer(update);
    }

	public void focusGenericPlayer(GenericPlayerUI owner) {
		rightTabPane.getSelectionModel().select(playersTab);
		playerListView.scrollTo(owner);
	}

	public void removeOtherPlayer(long id) {
		for(GenericPlayerUI i : playerListView.getItems()) {
			if(i.getPid() == id) {
				i.deleteGamePiece();
				playerListView.getItems().remove(i);
				break;
			}
		}
	}

    public void setTurn(TurnUpdate update) {
		if(!update.isExtension()) {
			final String turn = update.getPid() == Client.getRouter().getPid() ? "your" : update.getNick() + "'s";
			Client.getGui().displayChatMessage(MessageType.SYSTEM, "It is now " + turn + " turn.");

			turnTime = update.getTurnTime();

			if(update.getPid() == Client.getRouter().getPid()) {
				setActionsDisabled(false);
			}
		} else {
			turnTime += update.getTurnTime();
		}
    }

	@FXML
	private void onRollDicePressed(ActionEvent actionEvent) {
		Client.getRouter().send(PacketType.ROLL_DICE_REQ, true);
	}

	@FXML
	private void onOpenNewConnectionPressed(ActionEvent actionEvent) {
		SplashController.open(Client.getStage().getOwner());
	}

	@FXML
	private void onDisconnectPressed(ActionEvent actionEvent) {
		Client.getRouter().disconnectAndAlertServer();
	}
}
