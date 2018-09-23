package ph.adamw.calloji.client.gui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.monopoly.BoardUI;
import ph.adamw.calloji.data.Board;
import ph.adamw.calloji.packet.server.PSChat;
import ph.adamw.calloji.packet.server.PSNickEdit;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

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

	public void addMessageToList(Text txt) {
		Platform.runLater(() -> chatListView.getItems().add(txt));
	}

	@FXML
	public void initialize() {
		mainBorderPane.setCenter(boardUI);
		loadBoard(new Board());
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
}
