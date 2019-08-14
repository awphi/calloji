package ph.adamw.calloji.client;

import lombok.Getter;
import ph.adamw.calloji.packet.data.Board;
import ph.adamw.calloji.packet.data.NewTurnUpdate;
import ph.adamw.calloji.packet.data.Player;
import ph.adamw.calloji.packet.data.PlayerUpdate;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ClientCache {
    private Board board;
    private Map<Long, PlayerUpdate> players = new HashMap<>();

    private PlayerUpdate clientPlayer;

    private NewTurnUpdate lastTurnUpdate;

    public void cacheBoard(Board board) {
        this.board = board;
    }

    public void cachePlayer(PlayerUpdate update) {
        if(update.getId() == Client.getRouter().getPid()) {
            clientPlayer = update;
        }

        players.put(update.getId(), update);
    }

    public void cacheTurnUpdate(NewTurnUpdate update) {
        this.lastTurnUpdate = update;
    }

    public PlayerUpdate getOtherPlayer(long id) {
        return players.get(id);
    }

    public List<PropertyPlot> getOwnedPlots(Player player) {
        return player.getOwnedPlots(board);
    }
}
