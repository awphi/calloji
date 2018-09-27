package ph.adamw.calloji.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;
import ph.adamw.calloji.client.gui.GuiController;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.util.LoggerUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Client extends Application {
	@Getter
	private static ClientRouter router;

	@Getter
	private static GuiController gui;

	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("K:mm");

	private final static List<Text> messageQueue = new ArrayList<>();

	public static void main(String[] args) {
		// Establish logger defaults then instantiate everything that uses a logger
		LoggerUtils.setFormatting();
		LoggerUtils.setProperty("defaultLogLevel", "warn");
		LoggerUtils.establishLevels(args);

		router = new ClientRouter();

		//TODO remove this connecting here and use a splash screen
		try {
			router.connect("0.0.0.0", 8080);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Application.launch(args);
	}

	public static void printMessage(MessageType type, String txt) {
		final Text text = new Text("[" + dateFormat.format(new Date()) + "] " + txt);
		text.setFill(type.getColor());

		if(gui != null) {
			gui.addMessageToList(text);
		} else {
			messageQueue.add(text);
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Register fonts
		Font.loadFont(getClass().getResource("/fxml/roboto.ttf").toExternalForm(), 16);

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/gui.fxml"));
		Parent root = fxmlLoader.load();

		gui = fxmlLoader.getController();

		for(Text i : messageQueue) {
			gui.addMessageToList(i);
		}

		messageQueue.clear();

		final Scene scene = new Scene(root);

		stage.setTitle("Calloji Client by awphi");
		stage.setScene(scene);
		stage.show();
	}

	@Override
	public void stop(){
		if(router.isConnected()) {
			router.requestDisconnect();
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
				router.forceDisconnect();
				break;
			}
		}
	}
}
