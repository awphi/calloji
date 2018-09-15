package ph.adamw.calloji.client.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.server.PServerChat;
import ph.adamw.calloji.packet.server.PServerNickEdit;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Slf4j
public class GuiController implements Initializable {
	@FXML
	private TextField chatTextField;
	@FXML
	private ListView<Text> chatListView;

	@FXML
	@Getter
	private Menu nicknameMenu;

	public void addMessageToList(Text txt) {
		Platform.runLater(() -> {
			chatListView.getItems().add(txt);
		});
	}

	@FXML
	public void onChatSubmitted(ActionEvent actionEvent) {
		if(chatTextField.getText().isEmpty()) {
			return;
		}

		Client.getRouter().send(new PServerChat(chatTextField.getText()));
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
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Edit Nickname");
		dialog.setHeaderText("Enter a new nickname");
		dialog.setContentText("Please enter your desired nick:");

		Optional<String> result = dialog.showAndWait();

		result.ifPresent(name -> {
			if(!name.isEmpty()) {
				Client.getRouter().send(new PServerNickEdit(name));
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
