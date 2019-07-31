package ph.adamw.calloji.server.monopoly;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.data.CardUpdate;
import ph.adamw.calloji.packet.data.ChatMessage;
import ph.adamw.calloji.packet.data.MessageType;
import ph.adamw.calloji.packet.data.Player;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.monopoly.card.MonoCard;
import ph.adamw.calloji.server.monopoly.card.MonoCardPile;
import ph.adamw.calloji.util.GameConstants;

@Slf4j
@AllArgsConstructor
public class MonoPlayer {
    private final ClientConnection connection;

    @Getter
    private final MonoGame game;

    /* NEVER DIRECTLY MODIFY THE PLAYER OBJECT W/O UPDATING THE CLIENTS therefore try to use the helper methods in here */
    @Getter
    @Deprecated
    private final Player player;

    void send(PacketType type, Object content) {
        connection.send(type, content);
    }

    public long getConnectionId() {
        return connection.getId();
    }

    String getConnectionNick() {
        return connection.getNick();
    }

    public void moveForward(Integer x) {
        if(x == null) {
            log.debug("Plot index given was null when attempting to move - board is probably missing the given type!");
            return;
        }

        moveSpaces((x - player.getBoardPosition()) % 40);
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

    public void moveSpaces(int x) {
        // Passed GO
        if(player.boardPosition + x >= 40) {
            addMoney(200);
        }

        player.boardPosition = (player.boardPosition + x) % 40;
        final Plot plot = game.getMonoBoard().getIndexedPlot(player.boardPosition);
        final MonoPropertyPlot mono = game.getMonoBoard().getMonoPlot(plot);

        // i.e. if it's a property or street
        if(mono != null) {
            mono.landedOnBy(this, x);
        } else {
            switch (plot.getType()) {
                case GO:
                    addMoney(GameConstants.GO_MONEY);
                    sendMessage(MessageType.SYSTEM, "You have received £" + GameConstants.GO_MONEY + ".00 for passing \"Go\".");
                    break;
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

        game.updateBoardOnAllClients();
        game.updatePlayerOnAllClients(this);
    }

    public int getAssetsSellValue() {
        int v = 0;

        for(PropertyPlot i : getPlayer().getOwnedPlots(game.getMonoBoard().getBoard())) {
            v += i.getValue() / 2;
        }

        return v;
    }

    public int getBuildingsSellValue() {
        int v = 0;
        for(PropertyPlot i : getPlayer().getOwnedPlots(game.getMonoBoard().getBoard())) {
            if(i instanceof StreetPlot) {
                final StreetPlot y = (StreetPlot) i;
                v += y.getHouses() * (y.getBuildCost() / 2);
            }
        }

        return v;
    }

    public void setJailed(int y) {
        player.jailed = y;
        moveForward(game.getMonoBoard().indexOfFirstPlot(PlotType.JAIL));
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
        } else if(player.balance + getAssetsSellValue() + getBuildingsSellValue() >= money) {
            game.extendCurrentTurn(30);
            //TODO this method must block for 30 seconds while the player manages their assets
            //TODO (ForceAssetManagement) - if at the end of the block it's not enough then auto-sell stuff till we good
        } else {
            ret = player.balance;
            player.balance = 0;
            setBankrupt(true);
        }

        game.updatePlayerOnAllClients(this);
        return ret;
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
