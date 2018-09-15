package ph.adamw.calloji.client;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.packet.client.IClient;
import ph.adamw.calloji.packet.server.PServerHeartbeat;

@Slf4j
public class ClientPacketHandler implements IClient {
    private final ClientRouter router;

    ClientPacketHandler(ClientRouter router) {
        this.router = router;
    }

    @Override
    public void onConnected() {
        Client.printMessage(MessageType.SYSTEM, "Connected to server.");

        new Thread(() -> {
            while(router.isConnected()) {
                router.send(new PServerHeartbeat());

                try {
                    Thread.sleep(2500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDisconnectAcknowledged() {
        Client.printMessage(MessageType.SYSTEM, "Disconnected from server.");

        router.forceDisconnect();
    }

    @Override
    public void onChatReceived(String from, String message) {
        Client.printMessage(MessageType.CHAT, from + ": " + message);
    }

    @Override
    public void onNickChanged(String nick) {
        Client.printMessage(MessageType.SYSTEM, "Nickname successfully changed to " + nick + ".");

        Platform.runLater(() -> {
            Client.getGui().getNicknameMenu().setText(nick);
            Client.getGui().getNicknameMenu().show();
            Client.getGui().getNicknameMenu().hide();
        });
    }
}
