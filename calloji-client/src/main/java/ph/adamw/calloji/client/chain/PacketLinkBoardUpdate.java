package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.data.Board;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.JsonUtils;

public class PacketLinkBoardUpdate extends PacketLink {
    public PacketLinkBoardUpdate() {
        super(PacketType.BOARD_UPDATE);
    }

    @Override
    public void handle(PacketType type, JsonElement content) {
        Platform.runLater(() -> Client.getGui().loadBoard(JsonUtils.getObject(content, Board.class)));
    }
}
