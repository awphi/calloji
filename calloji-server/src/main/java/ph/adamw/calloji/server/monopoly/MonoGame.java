package ph.adamw.calloji.server.monopoly;

import com.google.common.collect.Iterables;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.data.*;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.server.ServerRouter;
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
    //TODO stop a player from being able to avoid rolling the dice

    @Getter
    private final MonoCardPile communityChestPile = new MonoCardPile("Community Chest", MonoCardPile.COMM_CHEST);

    @Getter
    private final MonoCardPile chancePile = new MonoCardPile("Chance", MonoCardPile.CHANCE);

    private Map<Long, MonoPlayer> playerMap = new TreeMap<>();

    @Getter
    private int currentTurnTime = GameConstants.TURN_TIME;

    @Getter
    private MonoPlayer currentTurnPlayer = null;

    @Getter
    private MonoBoard monoBoard = new MonoBoard(this);

    @Getter
    private boolean hasRolled = false;

    @Getter
    private MonoAuction activeAuction = null;

    private Integer rigged = null;

    @Setter
    @Getter
    private MonoPlayer bankruptee = null;

    private Thread gameThread;

    public MonoGame() {
        ServerRouter.getEventBus().register(this);
    }

    public MonoGame(MonoGame old) {
        this();
        this.playerMap = old.playerMap;
    }

    public void startGame() {
        gameThread = new Thread(this::runGame, "Game");
        gameThread.start();
    }

    private void runGame() {
        sendAllMessage(MessageType.ADMIN, "Game beginning!");

        final Iterator<Long> it = Iterables.cycle(playerMap.keySet()).iterator();
        while(getWinner() == null && !playerMap.isEmpty()) {
            currentTurnPlayer = playerMap.get(it.next());
            playTurn();
        }

        final MonoPlayer pl = getWinner();
        log.debug("Game over! Winner: " + pl);

        if(pl != null) {
            sendAllMessage(MessageType.ADMIN, "Game Over. " + pl.getConnectionNick() + " has won!", pl);
            pl.sendMessage(MessageType.ADMIN, "Congratulations. You have won!");
        }

        sendAllMessage(MessageType.ADMIN, "The game will restart in 30 seconds...");

        int timer = 30;
        while(timer > 0) {
            timer ++;

            if(timer <= 5) {
                sendAllMessage(MessageType.ADMIN, Integer.toString(timer));
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }


        final MonoGame game = new MonoGame(this);
        ServerRouter.setGame(game);
        if(playerMap.size() == ServerRouter.getClientPool().getCapacity()) {
            game.updateBoardOnAllClients();
            game.updateAllPlayersOnAllClients();
            game.startGame();
        }
    }

    public void rigDice(int x) {
        rigged = x;
    }

    public void extendCurrentTurn(int secs) {
        if(secs <= 0) {
            return;
        }

        currentTurnTime += secs;
        sendToAll(PacketType.TURN_EXTENSION, secs);
    }

    public void endCurrentTurn() {
        currentTurnTime = 0;
        gameThread.interrupt();
    }

    //TODO introduce trading to the game inside the players tab where u can trade assets + cash.
    private void playTurn() {
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

        sendToAll(PacketType.NEW_TURN, new NewTurnUpdate(currentTurnPlayer.getConnectionId(), currentTurnPlayer.getConnectionNick()));

        // Allows turns to be extended from a separate thread
        do {
            int cache = currentTurnTime;
            currentTurnTime = 0;

            try {
                Thread.sleep(cache * 1000);
            } catch (InterruptedException e) {
                //e.printStackTrace();
                break;
            }

        } while(currentTurnTime > 0);
    }

    public void rollDice(MonoPlayer player) {
        if(hasRolled) {
            return;
        }

        int roll = 0;

        if(rigged == null) {
            for(int i = 0; i < GameConstants.DICE_AMOUNT; i ++) {
                roll += ThreadLocalRandom.current().nextInt(1, GameConstants.DICE_SIDES + 1);
            }
        } else {
            roll = rigged;
        }

        rigged = null;
        sendAllMessage(MessageType.SYSTEM, player.getConnectionNick() + " rolled a " + roll + "!", player);
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
            startGame();
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
