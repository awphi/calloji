package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.data.Board;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.util.JsonUtils;

@Log4j2
@PacketLinkType(PacketType.BOARD_UPDATE)
public class PacketLinkBoardUpdate extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final Board board = JsonUtils.getObject(content, Board.class);
        Client.getCache().cacheBoard(board);
        Platform.runLater(() -> {
            Client.getGui().getBoardUI().load(board);
            Client.getGui().updateManagedAssets(board);
        });
    }
}
