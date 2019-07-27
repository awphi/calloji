package ph.adamw.calloji.client;

import lombok.Getter;
import ph.adamw.calloji.packet.data.Board;
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

    public void cacheBoard(Board board) {
        this.board = board;
    }

    public void cachePlayer(PlayerUpdate update) {
        players.put(update.getId(), update);
    }

    public PlayerUpdate getCachedPlayer(long id) {
        return players.get(id);
    }

    public List<PropertyPlot> getOwnedPlots(Player player) {
        return player.getOwnedPlots(board);
    }
}
