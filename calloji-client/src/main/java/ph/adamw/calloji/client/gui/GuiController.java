package ph.adamw.calloji.client.gui;

import com.google.gson.JsonPrimitive;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.ClientRouter;
import ph.adamw.calloji.client.StringUtil;
import ph.adamw.calloji.client.gui.monopoly.BoardUI;
import ph.adamw.calloji.client.gui.monopoly.GenericPlayerUI;
import ph.adamw.calloji.client.gui.monopoly.ThinPlotUI;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.*;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

import java.util.Optional;
import java.util.function.Predicate;

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

	private int turnTime = 0;

	@FXML
	private Label jailedLabel;

	@FXML
	private Label getOutOfJailsLabel;

	@FXML
	private Label balanceLabel;

	@Getter
	private Board boardCache;

	@FXML
	@Getter
	private MenuItem disconnectButton;

	public void addMessageToList(Label txt) {
		Platform.runLater(() -> chatListView.getItems().add(txt));
	}

	@FXML
	public void initialize() {
		Client.printMessage(MessageType.SYSTEM, "Welcome to Calloji!");

		playerListView.setSelectionModel(new NullSelectionModel<>());
		chatListView.setSelectionModel(new NullSelectionModel<>());
		playersBorderPane.setCenter(playerListView);
		mainBorderPane.setCenter(boardUI);

		final Thread timer = GuiUtils.startRunner(this::decrementTurnTimer, 1000);
		Client.getStage().setOnCloseRequest(event -> {
			timer.interrupt();
			Platform.exit();
			System.exit(0);
		});
	}

	private void decrementTurnTimer() {
		if(turnTime > 0) {
			turnTime --;
			turnTimer.setText(StringUtil.formatSecondMinutes(turnTime));

			if(turnTime == 0 && !rollDiceButton.isDisabled()) {
				Client.printMessage(MessageType.SYSTEM, "Time's up!");
				setActionsDisabled(true);
			}
		}
	}

	private void setActionsDisabled(boolean b) {
		//TODO complete this list w/ asset management buttons etc. once they're implemented
		rollDiceButton.setDisable(b);
	}

	@FXML
	public void onChatSubmitted(ActionEvent actionEvent) {
		if(chatTextField.getText().isEmpty()) {
			return;
		}

		Client.getRouter().send(PacketType.CHAT, new ChatMessage(chatTextField.getText(), ""));
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
		boardCache = board;
		boardUI.loadBoard(board);
	}

    public void loadPlayer(PlayerUpdate update) {
		if(update.getId() == Client.getRouter().getPid()) {
			balanceLabel.setText("Balance: £" + update.getPlayer().getBalance());
			getOutOfJailsLabel.setText("Get Out of Jail Cards: " + update.getPlayer().getGetOutOfJails());
			jailedLabel.setText("Jailed: " + update.getPlayer().getJailed());
		}

		for(GenericPlayerUI i : playerListView.getItems()) {
			if(i.getPid() == update.getId()) {
				i.reload(update);
				return;
			}
		}

		final GenericPlayerUI gen = new GenericPlayerUI(update, boardUI);

		if(update.getId() == Client.getRouter().getPid()) {
			playerListView.getItems().add(0, gen);
		} else {
			playerListView.getItems().add(gen);
		}
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
		Client.printMessage(MessageType.SYSTEM, "It is now the turn of " + update.getNick() + ".");
		turnTime = update.getTurnTime();

		if(update.getPid() == Client.getRouter().getPid()) {
			setActionsDisabled(false);
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
