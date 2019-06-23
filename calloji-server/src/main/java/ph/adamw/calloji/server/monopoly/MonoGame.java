package ph.adamw.calloji.server.monopoly;

import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.data.*;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.event.ClientConnectedEvent;
import ph.adamw.calloji.server.connection.event.ClientDisconnectedEvent;
import ph.adamw.calloji.server.connection.event.ClientNickChangeEvent;
import ph.adamw.calloji.server.connection.event.ClientPoolListener;
import ph.adamw.calloji.server.monopoly.card.MonoCardPile;

import javax.annotation.Nullable;
import java.util.*;

@Slf4j
public class MonoGame extends ClientPoolListener {
    @Getter
    private final MonoCardPile communityChestPile = new MonoCardPile(MonoCardPile.COMM_CHEST);

    @Getter
    private final MonoCardPile chancePile = new MonoCardPile(MonoCardPile.CHANCE);

    private final List<MonoPlayer> playerList = new ArrayList<>();

    private int currentTurnTime = 30;

    @Getter
    private MonoPlayer currentTurnPlayer = null;

    @Getter
    private MonoBoard monoBoard = new MonoBoard(this);

    private int nextTurnPlayerIndex = 0;

    @Getter
    private boolean inProgress = false;

    public void start() {
        if(inProgress) return;
        inProgress = true;

        while(getWinner() == null) {
            playTurn();
        }

        // TODO Declare winner packet, end game and discard
    }

    void extendCurrentTurn(int secs) {
        currentTurnTime += secs;
        updateAllPlayersOnAllClients();
    }

    private void playTurn() {
        while(currentTurnPlayer == null || !currentTurnPlayer.getPlayer().isBankrupt()) {
            currentTurnPlayer = playerList.get(nextTurnPlayerIndex);
            nextTurnPlayerIndex = (nextTurnPlayerIndex + 1) % playerList.size();
        }

        sendToAll(PacketType.TURN_UPDATE, new TurnUpdate(monoPlayer.getConnectionId(), currentTurnTime));
        currentTurnTime = 30;

        if(currentTurnPlayer.getPlayer().getJailed() > 0) {
            currentTurnTime = 0;
            currentTurnPlayer.decJailed();
        }

        // Allows turns to be extended from a separate thread
        while(currentTurnTime > 0) {
            currentTurnTime = 0;

            try {
                // Allow 30 seconds for the user to send the server packets (handled in a separate thread)
                Thread.sleep(currentTurnTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        currentTurnPlayer.send(PacketType.TURN_UPDATE, new JsonPrimitive(false));
    }

    private MonoPlayer getWinner() {
        int c = 0;
        MonoPlayer x = null;

        for(MonoPlayer i : playerList) {
            if(!i.getPlayer().isBankrupt()) {
                c ++;
                x = i;
            }
        }

        return c == 1 ? x : null;
    }

    @Override
    public void onClientConnect(ClientConnectedEvent e) {
        final MonoPlayer n = new MonoPlayer(e.getPool().get(e.getId()), this, new Player(GamePiece.next()));
        playerList.add(n);

        log.debug("Created new MonoPlayer for: " + e.getId());

        n.updateBoard();
        updateAllPlayersOnAllClients();

        if(playerList.size() == e.getPool().getCapacity()) {
            log.debug("Game has " + playerList.size() + " players - commencing game!");
            start();
        }
    }

    @Override
    public void onClientDisconnect(ClientDisconnectedEvent e) {
        final MonoPlayer mp = getMonoPlayer(e.getId());
        if(mp != null) {
            playerList.remove(mp);
            log.info("Deleting player " + mp.getConnectionId() + " due to a disconnect.");

            for(MonoPlayer i : playerList) {
                i.send(PacketType.CLIENT_CONNECTION_UPDATE, new ConnectionUpdate(true, mp.getConnectionId()));
            }
        }
    }

    @Override
    public void onClientNickChanged(ClientNickChangeEvent e) {
        final MonoPlayer mp = getMonoPlayer(e.getId());
        if(mp != null) {
            updatePlayerOnAllClients(mp);
        }
    }

    void updateBoardOnAllClients() {
        for(MonoPlayer i : playerList) {
            i.updateBoard();
        }
    }

    private void updateAllPlayersOnAllClients() {
        for(MonoPlayer i : playerList) {
            updatePlayerOnAllClients(i);
        }
    }

    void updatePlayerOnAllClients(MonoPlayer monoPlayer) {
        for(MonoPlayer i : playerList) {
            i.send(PacketType.PLAYER_UPDATE, new PlayerUpdate(monoPlayer.getPlayer(), monoPlayer.getConnectionId(), monoPlayer.getConnectionNick()));
        }
    }

    void sendToAll(PacketType type, Object element) {
        for(MonoPlayer player : playerList) {
            player.send(type, element);
        }
    }

    @Nullable
    MonoPlayer getMonoPlayer(Player owner) {
        for(MonoPlayer i : playerList) {
            if(i.getPlayer() == owner) {
                return i;
            }
        }

        return null;
    }

    @Nullable
    private MonoPlayer getMonoPlayer(long id) {
        for(MonoPlayer i : playerList) {
            if(i.getConnectionId() == id) {
                return i;
            }
        }

        return null;
    }
}
