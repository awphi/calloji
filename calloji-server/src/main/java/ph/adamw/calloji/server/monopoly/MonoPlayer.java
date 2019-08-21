package ph.adamw.calloji.server.monopoly;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.packet.data.*;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.monopoly.card.MonoCard;
import ph.adamw.calloji.server.monopoly.card.MonoCardPile;
import ph.adamw.calloji.util.GameConstants;

import java.util.Iterator;

@Log4j2
public class MonoPlayer {
    private final ClientConnection connection;

    @Getter
    private final MonoGame game;

    /* NEVER DIRECTLY MODIFY THE PLAYER OBJECT W/O UPDATING THE CLIENTS therefore try to use the helper methods in here */
    @Getter
    @Deprecated
    private final Player player;

    public MonoPlayer(ClientConnection connection, MonoGame game) {
        this.player = new Player(GamePiece.next(), connection.getId());
        this.game = game;
        this.connection = connection;
    }

    public MonoPlayer(MonoPlayer player, MonoGame monoGame) {
        this(player.connection, monoGame);
    }

    void send(PacketType type, Object content) {
        connection.send(type, content);
    }

    public long getConnectionId() {
        return connection.getId();
    }

    public String getConnectionNick() {
        return connection.getNick();
    }

    public void moveTo(Integer x) {
        if(x == null) {
            log.debug("Plot index given was null when attempting to move - board is probably missing the given type!");
            return;
        }

        moveSpaces(Math.floorMod(x - player.getBoardPosition(), 40));
    }

    public void addAsset(MonoPropertyPlot plot) {
        plot.getPlot().setOwner(getConnectionId());
        game.updateBoardOnAllClients();
        game.updatePlayerOnAllClients(this);
    }

    public void removeAsset(MonoPropertyPlot plot, boolean update) {
        plot.getPlot().setOwner(null);

        if(update) {
            game.updateBoardOnAllClients();
            game.updatePlayerOnAllClients(this);
        }
    }

    public void moveSpaces(final int spaces, boolean wasDice) {
        if(player.boardPosition + spaces >= 40 && player.getJailed() <= 0) {
            addMoney(GameConstants.GO_MONEY);
            sendMessage(MessageType.SYSTEM, "You have received £" + GameConstants.GO_MONEY + ".00 for passing Go.");
        }

        player.boardPosition = (player.boardPosition + spaces) % 40;

        game.updateBoardOnAllClients();

        if(spaces > 0) {
            player.lastMoveType = MoveType.FORWARD;
        } else if(spaces < 0) {
            player.lastMoveType = MoveType.BACKWARD;
        } else {
            player.lastMoveType = MoveType.NONE;
        }

        //TODO dice rolling anim using this with the moving anim as a callback from the dice roll anim finishing
        player.wasRoll = wasDice;

        game.updatePlayerOnAllClients(this);

        player.wasRoll = false;
        player.lastMoveType = MoveType.NONE;


        final Plot plot = game.getMonoBoard().getIndexedPlot(player.boardPosition);
        final MonoPropertyPlot mono = game.getMonoBoard().getMonoPlot(plot);

        // i.e. if it's a property or street
        if(mono != null) {
            mono.landedOnBy(this, spaces);
        } else {
            switch (plot.getType()) {
                case TAX:
                case SUPER_TAX:
                    final int amount = plot.getType().equals(PlotType.TAX) ? GameConstants.INCOME_TAX : GameConstants.SUPER_TAX;
                    tryRemoveMoney(amount);
                    sendMessage(MessageType.SYSTEM, "You have paid £" + amount + ".00 in tax to the bank.");
                    break;
                case GO_TO_JAIL:
                    setJailed(GameConstants.GO_TO_JAIL_TURNS);
                    sendMessage(MessageType.WARNING, "You have been sent to jail for " + GameConstants.GO_TO_JAIL_TURNS + " turns.");
                    game.sendAllMessage(MessageType.SYSTEM, getConnectionNick() + " has been sent to jail for "+ GameConstants.GO_TO_JAIL_TURNS + " turns!", this);
                    break;
                case CHANCE:
                case COMMUNITY_CHEST: {
                    final MonoCardPile pile;

                    if (plot.getType().equals(PlotType.CHANCE)) {
                        pile = game.getChancePile();
                    } else {
                        pile = game.getCommunityChestPile();
                    }

                    final MonoCard card = pile.draw();
                    card.handle(this);
                    send(PacketType.CARD_DRAWN, new CardUpdate(pile.getName(), card.getText()));
                    game.sendAllMessage(MessageType.SYSTEM, getConnectionNick() + " has drawn a " + pile.getName() +  " card!", this);
                }
                break;
            }
        }
    }

    public void moveSpaces(final int spaces) {
        moveSpaces(spaces, false);
    }

    public int getAssetsMortgageValue() {
        int v = 0;

        for(PropertyPlot i : getPlayer().getOwnedPlots(game.getMonoBoard().getBoard())) {
            v += i.getValue() / 2;
        }

        return v;
    }

    public int getBuildingsSellValue() {
        int v = 0;
        for(StreetPlot i : getPlayer().getOwnedStreetPlots(game.getMonoBoard().getBoard())) {
            v += i.getHouses() * (i.getBuildCost() / 2);
        }

        return v;
    }

    public void setJailed(int y) {
        player.jailed = y;
        moveTo(game.getMonoBoard().indexOfFirstPlot(PlotType.JAIL));
        game.updatePlayerOnAllClients(this);
    }

    public void addMoney(int money) {
        if(money > 0) {
            player.balance += money;
            game.updatePlayerOnAllClients(this);
        }
    }

    void decJailed(int amount) {
        player.jailed -= amount;
        game.updatePlayerOnAllClients(this);
    }

    void setBankrupt(boolean b) {
        if(b) {
            for(PropertyPlot i : getPlayer().getOwnedPlots(game.getMonoBoard().getBoard())) {
                removeAsset(game.getMonoBoard().getMonoPlot(i), false);
            }
        }

        player.isBankrupt = b;
        game.updateBoardOnAllClients();
        game.updatePlayerOnAllClients(this);
        game.sendAllMessage(MessageType.WARNING, getConnectionNick() + " has gone bankrupt!", this);
        sendMessage(MessageType.WARNING, "You have gone bankrupt! You can continue to watch the game but no longer play.");
    }

    public int tryRemoveMoney(int money) {
        if(money <= 0) {
            return 0;
        }

        int ret = money;

        if(player.balance >= money) {
            player.balance -= money;
        } else if(player.balance + getAssetsMortgageValue() + getBuildingsSellValue() >= money) {
            game.extendCurrentTurn(30);
            sendMessage(MessageType.WARNING, "You are about to go bankrupt! You have been given 30 seconds to manage your assets to obtain at least £" + money + ".00!");
            send(PacketType.FORCE_MANAGE_ASSETS, new JsonObject());
            game.setBankruptee(this);

            // Block for 30 seconds
            int timer = 30;
            while(timer > 0) {
                timer ++;

                if(timer <= 10) {
                    sendMessage(MessageType.WARNING, Integer.toString(timer));
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }

            // Now we check if they've managed their assets
            if(player.balance >= money) {
                player.balance -= money;
            } else {
                autosellAssets(money);
            }
        } else {
            ret = player.balance;
            player.balance = 0;
            setBankrupt(true);
        }

        game.setBankruptee(null);
        game.updatePlayerOnAllClients(this);
        return ret;
    }

    @SuppressWarnings("unchecked")
    private void autosellAssets(int moneyRequired) {
        final Board board = game.getMonoBoard().getBoard();

        // Attempt to sell off houses first
        while(player.balance < moneyRequired && getBuildingsSellValue() > 0) {
            final Iterator<StreetPlot> streetPlots = player.getOwnedStreetPlots(board).iterator();

            do {
                final StreetPlot plot = streetPlots.next();

                if (plot.canSellHouse(player, board)) {
                    ((MonoStreetPlot) game.getMonoBoard().getMonoPlot(plot)).sellHouse();
                }
            } while (streetPlots.hasNext() && player.balance < moneyRequired);
        }

        // Now we try mortgaging stuff
        while(player.balance < moneyRequired && getAssetsMortgageValue()> 0) {
            final Iterator<PropertyPlot> plots = player.getOwnedPlots(board).iterator();

            do {
                final PropertyPlot plot = plots.next();

                if (!plot.isMortgaged() && !plot.isBuiltOn()) {
                    game.getMonoBoard().getMonoPlot(plot).mortgage();
                }
            } while (plots.hasNext() && player.balance < moneyRequired);
        }

        game.updateBoardOnAllClients();
        game.updatePlayerOnAllClients(this);
        sendMessage(MessageType.WARNING, "Some of your assets have been automatically sold/mortgaged to avoid bankruptcy as you failed to respond in time.");
    }

    public void updateBoard() {
        send(PacketType.BOARD_UPDATE, game.getMonoBoard().getBoard());
    }

    public void sendMessage(MessageType messageType, String text) {
        send(PacketType.CHAT_MESSAGE, new ChatMessage(messageType, text, ""));
    }

    public void setGetOutOfJails(int i) {
        player.getOutOfJails = i;
        game.updatePlayerOnAllClients(this);
    }
}
