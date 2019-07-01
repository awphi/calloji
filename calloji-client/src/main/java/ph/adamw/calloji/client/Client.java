package ph.adamw.calloji.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import ph.adamw.calloji.client.gui.AuctionGuiController;
import ph.adamw.calloji.client.gui.GuiController;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.client.gui.SplashController;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
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

	@Getter
	private static Stage stage;

	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("K:mm");

	private final static List<Text> messageQueue = new ArrayList<>();

	public static void main(String[] args) {
		// Establish logger defaults then instantiate everything that uses a logger
		LoggerUtils.setFormatting();
		LoggerUtils.setProperty("defaultLogLevel", "warn");
		LoggerUtils.establishLevels(args);

		router = new ClientRouter();
		Application.launch(args);
	}

	public static void printMessage(MessageType type, String txt) {
		final Text text = new Text("[" + dateFormat.format(new Date()) + "] " + txt);
		text.setFill(type.getColor());

		if (gui != null) {
			gui.addMessageToList(text);
		} else {
			messageQueue.add(text);
		}
	}


	public static boolean attemptConnect(String host, int port) {
		try {
			router.connect(host, port);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public void start(Stage stage) throws Exception {
		Client.stage = stage;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/gui.fxml"));
		Parent root = fxmlLoader.load();

		gui = fxmlLoader.getController();

		for(Text i : messageQueue) {
			gui.addMessageToList(i);
		}

		messageQueue.clear();

		final Scene scene = new Scene(root);

		stage.setTitle("Calloji Client [awphi]");
		stage.setScene(scene);
		stage.setOnShown(event -> SplashController.open((Window) event.getSource()));
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
