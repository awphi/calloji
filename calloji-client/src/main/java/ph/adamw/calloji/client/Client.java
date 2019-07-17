package ph.adamw.calloji.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import ph.adamw.calloji.client.gui.GuiController;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.client.gui.SplashController;
import ph.adamw.calloji.util.LoggerUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends Application {
	@Getter
	private static ClientRouter router;

	@Getter
	private static GuiController gui;

	@Getter
	private static Stage stage;

	public static void main(String[] args) {
		// Must init the logger before instantiating objects that use it
		LoggerUtils.init(args);

		router = new ClientRouter();
		Application.launch(args);
	}

	public static boolean attemptConnect(String host, int port) {
		try {
			router.connect(host, port);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		Platform.runLater(() -> Client.getGui().getDisconnectButton().setDisable(false));
		return true;
	}

	@Override
	public void start(Stage stage) throws Exception {
		Client.stage = stage;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/gui.fxml"));
		Parent root = fxmlLoader.load();

		gui = fxmlLoader.getController();

		final Scene scene = new Scene(root);

		stage.setTitle("Calloji Client [awphi]");
		stage.setScene(scene);
		stage.setOnShown(event -> SplashController.open((Window) event.getSource()));
		stage.show();
	}

	@Override
	public void stop(){
		if(router.isConnected()) {
			router.disconnectAndAlertServer();
		}

		int timer = 0;

		// Loop will terminate if the onDisconnect req is fulfilled or will force quit after 5 seconds and let the server
		// handle it as a lost connection.
		while(router.isConnected()) {
			timer ++;

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(timer == 5) {
				router.disconnect();
				break;
			}
		}
	}
}
