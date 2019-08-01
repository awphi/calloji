package ph.adamw.calloji.server.monopoly;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.data.*;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.connection.event.ClientConnectedEvent;
import ph.adamw.calloji.server.connection.event.ClientDisconnectedEvent;
import ph.adamw.calloji.server.connection.event.ClientNickChangeEvent;
import ph.adamw.calloji.server.monopoly.card.MonoCardPile;
import ph.adamw.calloji.util.GameConstants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Log4j2
public class MonoGame {
    public MonoGame() {
        ServerRouter.getEventBus().register(this);
    }

    @Getter
    private final MonoCardPile communityChestPile = new MonoCardPile("Community Chest", MonoCardPile.COMM_CHEST);

    @Getter
    private final MonoCardPile chancePile = new MonoCardPile("Chance", MonoCardPile.CHANCE);

    private final Map<Long, MonoPlayer> playerMap = new HashMap<>();

    private int currentTurnTime = GameConstants.TURN_TIME;

    @Getter
    private MonoPlayer currentTurnPlayer = null;

    @Getter
    private MonoBoard monoBoard = new MonoBoard(this);

    private int nextTurnPlayerIndex = 0;

    private boolean hasRolled = false;

    @Getter
    private MonoAuction activeAuction;

    private Integer rigged = null;

    public void start() {
        while(getWinner() == null && !playerMap.isEmpty()) {
            playTurn();
        }

        log.debug("Game over! Winner: " + getWinner());
        // TODO Declare winner packet + shut down server (or restart??)
    }

    public void rigDice(int x) {
        rigged = x;
    }

    void extendCurrentTurn(int secs) {
        currentTurnTime += secs;
        sendToAll(PacketType.TURN_UPDATE, new TurnUpdate(currentTurnPlayer.getConnectionId(), secs, currentTurnPlayer.getConnectionNick(), true));
    }

    //TODO introduce trading to the game inside the players tab where u can trade assets + cash.
    private void playTurn() {
        final Iterator<Long> it = playerMap.keySet().iterator();
        while(currentTurnPlayer == null || currentTurnPlayer.getPlayer().isBankrupt()) {
            currentTurnPlayer = getMonoPlayer(it.next());
            nextTurnPlayerIndex = (nextTurnPlayerIndex + 1) % playerMap.size();
        }

        log.debug("Turn of player: " + currentTurnPlayer);

        hasRolled = false;
        currentTurnTime = GameConstants.TURN_TIME;

        if(currentTurnPlayer.getPlayer().getJailed() > 0) {
            if(currentTurnPlayer.getPlayer().getGetOutOfJails() > 0) {
                currentTurnPlayer.getPlayer().getOutOfJails --;
                currentTurnPlayer.decJailed(currentTurnPlayer.getPlayer().getJailed());

                currentTurnPlayer.sendMessage(MessageType.SYSTEM, "You automatically used your get out of jail free card to escape.");
                sendAllMessage(MessageType.SYSTEM, currentTurnPlayer.getConnectionNick() + " used their get out of jail free card.", currentTurnPlayer);
            } else {
                currentTurnTime = 0;
                currentTurnPlayer.decJailed(1);

                sendAllMessage(MessageType.SYSTEM, "Skipping turn of " + currentTurnPlayer.getConnectionNick() + " as they are in jail!");
            }
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

        final int roll = rigged != null ? rigged : ThreadLocalRandom.current().nextInt(1, GameConstants.DICE_AMOUNT * GameConstants.DICE_SIDES + 1);
        rigged = null;
        final MonoPlayer player = getMonoPlayer(connection.getId());
        sendAllMessage(MessageType.SYSTEM, connection.getNick() + " rolled a " + roll + "!", player);
        player.sendMessage(MessageType.SYSTEM, "You rolled a " + roll + "!");
        player.moveSpaces(roll);
        hasRolled = true;
    }

    public void auction(PropertyPlot property, MonoPlayer payee) {
        extendCurrentTurn(GameConstants.AUCTION_TIME);
        sendToAll(PacketType.AUCTION_START, property);
        final String owner = payee == null ? "The bank" : payee.getConnectionNick();
        sendAllMessage(MessageType.SYSTEM, owner + " has placed " + property.getName() + " on public auction.");
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

                if(payee != null) {
                    payee.addMoney(activeAuction.getWinnerBid());
                    payee.sendMessage(MessageType.SYSTEM, "You received a payment of £" + activeAuction.getWinnerBid() + ".00 from " + activeAuction.getWinner().getConnectionNick() + " for " + property.getName() + ".");
                }
            }

            activeAuction = null;
        }, "Auc").start();
    }

    private MonoPlayer getWinner() {
        // To allow for 1-person games (for debugging)
        if(playerMap.size() == 1) {
            final MonoPlayer first = getMonoPlayer(playerMap.keySet().iterator().next());
            return first.getPlayer().isBankrupt() ? first : null;
        }

        int c = 0;
        MonoPlayer x = null;

        for(MonoPlayer i : playerMap.values()) {
            if(!i.getPlayer().isBankrupt()) {
                c ++;
                x = i;
            }
        }

        return c == 1 ? x : null;
    }

    @Subscribe
    public void onClientConnect(ClientConnectedEvent e) {
        final MonoPlayer n = new MonoPlayer(e.getPool().get(e.getId()), this, new Player(GamePiece.next(), e.getId()));
        playerMap.put(e.getId(), n);

        log.debug("Created new MonoPlayer for: " + e.getId());

        n.updateBoard();
        updateAllPlayersOnAllClients();

        sendAllMessage(MessageType.SYSTEM, n.getConnectionNick() + " has joined the game!", n);

        if(playerMap.size() == e.getPool().getCapacity()) {
            log.debug("Game has " + playerMap.size() + " players - commencing game!");
            sendAllMessage(MessageType.SYSTEM, "Game beginning!");
            start();
        } else {
            sendAllMessage(MessageType.SYSTEM, "Waiting for " + (e.getPool().getCapacity() - playerMap.size()) + " more player before beginning the game.");
        }
    }

    @Subscribe
    public void onClientDisconnect(ClientDisconnectedEvent e) {
        final MonoPlayer mp = getMonoPlayer(e.getId());
        if(mp != null) {
            playerMap.remove(mp.getConnectionId());
            log.info("Deleting player " + mp.getConnectionId() + " due to a disconnect.");
            sendAllMessage(MessageType.WARNING, mp.getConnectionNick() + " has left the game.");

            for(MonoPlayer i : playerMap.values()) {
                i.send(PacketType.CLIENT_CONNECTION_UPDATE, new ConnectionUpdate(true, mp.getConnectionId()));
            }
        }
    }

    @Subscribe
    public void onClientNickChanged(ClientNickChangeEvent e) {
        updateBoardOnAllClients();

        final MonoPlayer mp = getMonoPlayer(e.getId());
        if(mp != null) {
            updatePlayerOnAllClients(mp);
        }
    }

    void updateBoardOnAllClients() {
        for(MonoPlayer i : playerMap.values()) {
            i.updateBoard();
        }
    }

    private void updateAllPlayersOnAllClients() {
        for(MonoPlayer i : playerMap.values()) {
            updatePlayerOnAllClients(i);
        }
    }

    void updatePlayerOnAllClients(MonoPlayer monoPlayer) {
        for(MonoPlayer i : playerMap.values()) {
            i.send(PacketType.PLAYER_UPDATE, new PlayerUpdate(monoPlayer.getPlayer(), monoPlayer.getConnectionId(), monoPlayer.getConnectionNick()));
        }
    }

    void sendToAll(PacketType type, Object element) {
        for(MonoPlayer player : playerMap.values()) {
            player.send(type, element);
        }
    }

    public void sendAllMessage(MessageType type, String txt, MonoPlayer... excludes) {
        final List<MonoPlayer> list = Arrays.asList(excludes);

        for(MonoPlayer player : playerMap.values()) {
            if(!list.contains(player)) {
                player.sendMessage(type, txt);
            }
        }
    }

    @Nullable
    public MonoPlayer getMonoPlayer(Long id) {
        return playerMap.get(id);
    }

    public Collection<MonoPlayer> getPlayers() {
        return playerMap.values();
    }
}
