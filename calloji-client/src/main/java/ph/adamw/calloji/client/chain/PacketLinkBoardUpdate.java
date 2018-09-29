package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.data.Board;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.BOARD_UPDATE)
public class PacketLinkBoardUpdate extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        Platform.runLater(() -> Client.getGui().loadBoard(JsonUtils.getObject(content, Board.class)));
    }
}
