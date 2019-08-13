package ph.adamw.calloji.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import ph.adamw.calloji.client.gui.GuiController;

import java.io.IOException;

public class Client extends Application {
	@Getter
	private static ClientRouter router;

	@Getter
	private static GuiController gui;

	@Getter
	private static Stage stage;

	@Getter
	private static ClientCache cache = new ClientCache();

	public static void main(String[] args) throws IOException {
		router = new ClientRouter();
		Application.launch(args);
	}

	@Override
	public void start(Stage stage) {
		Client.stage = stage;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/gui.fxml"));
		Parent root = null;
		try {
			root = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		gui = fxmlLoader.getController();

		final Scene scene = new Scene(root);

		stage.setTitle("Calloji Client [awphi]");
		stage.setScene(scene);
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
		while(router.isConnected() && timer < 5) {
			timer ++;

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		router.disconnect();
		System.exit(0);
	}
}
