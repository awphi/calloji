package ph.adamw.calloji.server.monopoly;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.data.Board;
import ph.adamw.calloji.server.connection.event.ClientConnectedEvent;
import ph.adamw.calloji.server.connection.event.ClientDisconnectedEvent;
import ph.adamw.calloji.server.connection.event.ClientPoolListener;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MonoGame extends ClientPoolListener {
    private final List<MonoPlayer> players = new ArrayList<>();

    private int nextTurn = 0;
    private int currentTurnTime = 30;

    @Getter
    private Board board = new Board();

    @Getter
    private boolean inProgress = false;

    private void start() {
        if(inProgress) return;
        inProgress = true;

        while(getWinner() == null) {
            playTurn();
        }

        // TODO Declare winner packet, end game and discard
    }

    public void extendCurrentTurn(int secs) {
        currentTurnTime += secs;
        updateClientUIs();
    }

    private void playTurn() {
        MonoPlayer player = players.get(nextTurn);

        while(!player.isBankrupt()) {
            nextTurn = (nextTurn + 1) % players.size();
            player = players.get(nextTurn);
        }

        //TODO TurnUpdate(true)
        currentTurnTime = 30;

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

        //TODO TurnUpdate(false)

        nextTurn = (nextTurn + 1) % players.size();
    }

    private MonoPlayer getWinner() {
        int c = 0;
        MonoPlayer x = null;

        for(MonoPlayer i : players) {
            if(!i.isBankrupt()) {
                c ++;
                x = i;
            }
        }

        return c == 1 ? x : null;
    }

    @Override
    public void onClientConnect(ClientConnectedEvent e) {
        players.add(new MonoPlayer(e.getPool().get(e.getId()), this));

        log.debug("Created new MonoPlayer for: " + e.getId());

        if(players.size() == e.getPool().getCapacity()) {
            log.debug("MonoGame has " + players.size() + " players - commencing game!");
            new Thread(this::start);
        }
    }

    @Override
    public void onClientDisconnect(ClientDisconnectedEvent e) {
        for(MonoPlayer i : players) {
            if(i.getConnectionId() == e.getId()) {
                players.remove(i);
                log.debug("Deleted MonoPlayer for: " + e.getId());
                break;
            }
        }
    }

    public void updateClientBoards() {
        for(MonoPlayer i : players) {
            i.updateClientBoard();
        }
    }

    public void updateClientUIs() {
        for(MonoPlayer i : players) {
            i.updateClientUI();
        }
    }
}
