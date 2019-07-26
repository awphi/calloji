package ph.adamw.calloji.server.monopoly;

import com.google.gson.JsonPrimitive;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.data.*;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.connection.event.ClientConnectedEvent;
import ph.adamw.calloji.server.connection.event.ClientDisconnectedEvent;
import ph.adamw.calloji.server.connection.event.ClientNickChangeEvent;
import ph.adamw.calloji.server.connection.event.ClientPoolListener;
import ph.adamw.calloji.server.monopoly.card.MonoCardPile;
import ph.adamw.calloji.util.GameConstants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class MonoGame implements ClientPoolListener {
    public MonoGame() {
        ServerRouter.getEventBus().register(this);
    }

    @Getter
    private final MonoCardPile communityChestPile = new MonoCardPile("Community Chest", MonoCardPile.COMM_CHEST);

    @Getter
    private final MonoCardPile chancePile = new MonoCardPile("Chance", MonoCardPile.CHANCE);

    private final List<MonoPlayer> playerList = new ArrayList<>();

    private int currentTurnTime = GameConstants.TURN_TIME;

    @Getter
    private MonoPlayer currentTurnPlayer = null;

    @Getter
    private MonoBoard monoBoard = new MonoBoard(this);

    private int nextTurnPlayerIndex = 0;

    private boolean hasRolled = false;

    @Getter
    private MonoAuction activeAuction;

    public void start() {
        while(getWinner() == null && !playerList.isEmpty()) {
            playTurn();
        }

        log.debug("Game over! Winner: " + getWinner());
        // TODO Declare winner packet + shut down server (or restart??)
    }

    void extendCurrentTurn(int secs) {
        currentTurnTime += secs;
        sendToAll(PacketType.TURN_UPDATE, new TurnUpdate(currentTurnPlayer.getConnectionId(), secs, currentTurnPlayer.getConnectionNick(), true));
    }

    private void playTurn() {
        while(currentTurnPlayer == null || currentTurnPlayer.getPlayer().isBankrupt()) {
            currentTurnPlayer = playerList.get(nextTurnPlayerIndex);
            nextTurnPlayerIndex = (nextTurnPlayerIndex + 1) % playerList.size();
        }
        log.debug("Turn of player: " + currentTurnPlayer);

        hasRolled = false;
        currentTurnTime = GameConstants.TURN_TIME;

        if(currentTurnPlayer.getPlayer().getJailed() > 0) {
            currentTurnTime = 0;
            currentTurnPlayer.decJailed();
        }

        sendToAll(PacketType.TURN_UPDATE, new TurnUpdate(currentTurnPlayer.getConnectionId(), currentTurnTime, currentTurnPlayer.getConnectionNick(), false));

        // Allows turns to be extended from a separate thread
        do {
            int cache = currentTurnTime;
            currentTurnTime = 0;

            try {
                Thread.sleep(cache * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while(currentTurnTime > 0);
    }

    public void rollDice(ClientConnection connection) {
        if(hasRolled) {
            return;
        }

        final int roll = ThreadLocalRandom.current().nextInt(1, GameConstants.DICE_AMOUNT * GameConstants.DICE_SIDES + 1);
        connection.send(PacketType.DICE_ROLL_RESPONSE, new JsonPrimitive(roll));
        getMonoPlayer(connection.getId()).moveSpaces(roll);
        hasRolled = true;
    }

    public void auction(PropertyPlot property) {
        extendCurrentTurn(GameConstants.AUCTION_TIME);
        sendToAll(PacketType.AUCTION_START, property);
        activeAuction = new MonoAuction(property);

        // Will discard of auction and deal w/ winner
        new Thread(() -> {
            try {
                Thread.sleep(GameConstants.AUCTION_TIME * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(activeAuction.getWinner() != null) {
                activeAuction.getWinner().tryRemoveMoney(activeAuction.getWinnerBid());
                activeAuction.getWinner().addAsset(monoBoard.getMonoPlot(activeAuction.getProperty()));
            }

            activeAuction = null;
        }).start();
    }

    private MonoPlayer getWinner() {
        // To allow for 1-person games (for debugging)
        if(playerList.size() == 1) {
            return playerList.get(0).getPlayer().isBankrupt() ? playerList.get(0) : null;
        }

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
        final MonoPlayer n = new MonoPlayer(e.getPool().get(e.getId()), this, new Player(GamePiece.next(), e.getId()));
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
    public MonoPlayer getMonoPlayer(Long id) {
        for(MonoPlayer i : playerList) {
            if(id.equals(i.getConnectionId())) {
                return i;
            }
        }

        return null;
    }
}
