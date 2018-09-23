package ph.adamw.calloji.packet.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ph.adamw.calloji.data.Board;

@AllArgsConstructor
@Getter
public class PCBoardUpdate extends PC {
    private final Board board;

    @Override
    public void handle(IClient client) {
        client.onBoardUpdate(board);
    }
}
