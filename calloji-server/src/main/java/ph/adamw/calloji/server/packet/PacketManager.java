package ph.adamw.calloji.server.packet;

import ph.adamw.calloji.server.ClientConnection;
import ph.adamw.calloji.server.Server;

public class PacketManager {
    public static void listenForPackets() {
        new Thread(PacketManager::listen).start();
    }

    private static void listen() {
        while(true) {
            for (ClientConnection cc : Server.getConnections()) {
                if (cc.receivePacket() != null) {

                }
            }
        }
    }

    private static void handlePacket() {

    }
}
