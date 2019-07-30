package ph.adamw.calloji.server.monopoly;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.data.CardUpdate;
import ph.adamw.calloji.packet.data.Player;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.monopoly.card.MonoCard;
import ph.adamw.calloji.server.monopoly.card.MonoCardPile;

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

    //TODO introduce a new generic system message packet to send to clients with a server message to display in chat
    // i.e. when a player pulls a chance/comm chest, when a player pays rent, if their turn is skipped due to jail,
    // auctions etc. etc.
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
                    addMoney(200);
                    break;
                case TAX:
                    tryRemoveMoney(100);
                    break;
                case SUPER_TAX:
                    tryRemoveMoney(200);
                    break;
                case GO_TO_JAIL:
                    setJailed(3);
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
    }

    public int tryRemoveMoney(int money) {
        int ret = money;

        if(player.balance >= money) {
            player.balance -= money;
        } else if(player.balance + getAssetsSellValue() + getBuildingsSellValue() >= money) {
            game.extendCurrentTurn(30);
            //TODO (ForceAssetManagement) - if they exit the menu or dont send a response in x seconds then auto-sell stuff till we good
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

    public void setGetOutOfJails(int i) {
        player.getOutOfJails = i;
        game.updatePlayerOnAllClients(this);
    }
}
