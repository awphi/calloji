package ph.adamw.calloji.client.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.monopoly.BoardUI;
import ph.adamw.calloji.client.gui.monopoly.GenericPlayerUI;
import ph.adamw.calloji.data.Board;
import ph.adamw.calloji.data.PlayerUpdate;
import ph.adamw.calloji.packet.server.PSChat;
import ph.adamw.calloji.packet.server.PSNickEdit;

import java.util.Optional;

@Slf4j
public class GuiController {
	@FXML
	private TextField chatTextField;

	@FXML
	private ListView<Text> chatListView;

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

	public void addMessageToList(Text txt) {
		Platform.runLater(() -> chatListView.getItems().add(txt));
	}

	@FXML
	public void initialize() {
		Client.printMessage(MessageType.SYSTEM, "Welcome to Calloji!");
		playerListView.setFixedCellSize(GenericPlayerUI.GAME_PIECE_SIZE);

		playersBorderPane.setCenter(playerListView);
		mainBorderPane.setCenter(boardUI);

		//TODO -- DEBUG ZONE --
		/*
		boardUI.loadBoard(new Board());

		Player p = new Player(GamePiece.next());

		Player p2 = new Player(GamePiece.next());
		p2.setCachedPid(21);
		*/
	}

	@FXML
	public void onChatSubmitted(ActionEvent actionEvent) {
		if(chatTextField.getText().isEmpty()) {
			return;
		}

		Client.getRouter().send(new PSChat(chatTextField.getText()));
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

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> {
			if(!name.isEmpty()) {
				Client.getRouter().send(new PSNickEdit(name));
			}
		});
	}

	public void loadBoard(Board board) {
		boardUI.loadBoard(board);
	}

    public void loadGenericPlayer(PlayerUpdate update) {
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

	public void loadThisPlayer(PlayerUpdate player) {
		// Add us to the player list and sets up our board piece
		loadGenericPlayer(player);
		// TODO - load our info and properties into gui in some way
	}

    public void setOurTurn(boolean isInputAllowed) {
		// TODO
    }

	public void focusGenericPlayer(GenericPlayerUI owner) {
		rightTabPane.getSelectionModel().select(playersTab);
		playerListView.scrollTo(owner);
		playerListView.getSelectionModel().select(owner);
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
}
