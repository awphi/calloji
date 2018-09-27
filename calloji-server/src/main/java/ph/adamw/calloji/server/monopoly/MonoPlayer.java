package ph.adamw.calloji.server.monopoly;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.data.Player;
import ph.adamw.calloji.data.plot.Plot;
import ph.adamw.calloji.data.plot.PlotType;
import ph.adamw.calloji.data.plot.PropertyPlot;
import ph.adamw.calloji.data.plot.StreetPlot;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.server.connection.ClientConnection;

@Slf4j
@AllArgsConstructor
class MonoPlayer {
    private final ClientConnection connection;

    private final Gson gson = new Gson();

    @Getter
    private final MonoGame game;

    @Getter
    private final Player player;

    void send(PacketType type, Object content) {
        connection.send(type, content);
    }

    long getConnectionId() {
        return connection.getId();
    }

    String getConnectionNick() {
        return connection.getNick();
    }

    private void moveTo(Integer x) {
        if(x == null) {
            log.debug("Plot index given was null when attempting to move - board is probably missing the given type!");
            return;
        }

        moveSpaces((40 % player.getBoardPosition()) + x);
    }

    private void moveSpaces(int x) {
        player.boardPosition = (player.boardPosition + x) % 40;
        final Plot plot = game.getMonoBoard().getBoard().plotAt(player.boardPosition);

        game.updatePlayerOnAllClients(this);

        if(plot instanceof PropertyPlot) {
            final PropertyPlot p = (PropertyPlot) plot;

            if(p.getOwner() == null) {
                // Offer to buy (if they have the money) or auction it
            } else if(p.getOwner() != player && !p.isMortgaged()){
                int rem = 0;

                if(p instanceof StreetPlot) {
                    final StreetPlot st = (StreetPlot) p;
                     rem = tryRemoveMoney(st.getRent());
                } else {
                    if(p.getType() == PlotType.UTILITY) {
                        final int owned = p.getOwner().getOwnedType(PlotType.UTILITY);
                        rem = tryRemoveMoney(owned * 5 + (owned - 2) * x);
                    } else if(p.getType() == PlotType.STATION) {
                        final int owned = p.getOwner().getOwnedType(PlotType.STATION);
                        rem = tryRemoveMoney(25 * (int) Math.pow(2, owned - 1));
                    }
                }

                if(player.isBankrupt()) {
                    // Since we can guarantee we're on the server side we can cast over the player
                    game.getMonoPlayer(p.getOwner()).addMoney(rem);
                }

                game.updatePlayerOnAllClients(this);
            }
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
                case CHANCE:
                    //TODO
                    break;
                case COMMUNITY_CHEST:
                    //TODO
                    break;
                case GO_TO_JAIL:
                    setJailed(3);
                    moveTo(game.getMonoBoard().indexOfFirstPlot(PlotType.JAIL));
                    break;
            }
        }

        game.updateBoardOnAllClients();
    }

    public int getAssetsSellValue() {
        int v = 0;
        for(Plot i : player.getOwnedPlots()) {
            if(i instanceof PropertyPlot) {
                final PropertyPlot p = (PropertyPlot) i;
                v += p.getValue() / 2;
            }
        }

        return v;
    }

    public int getBuildingsSellValue() {
        int v = 0;
        for(Plot i : player.getOwnedPlots()) {
            if(i instanceof StreetPlot) {
                final StreetPlot y = (StreetPlot) i;
                v += y.getHouses() * (y.getBuildCost() / 2);
            }
        }

        return v;
    }

    private void setJailed(int y) {
        player.jailed = y;
        game.updatePlayerOnAllClients(this);
    }

    void addMoney(int money) {
        player.balance += money;
        game.updatePlayerOnAllClients(this);
    }

    void decJailed() {
        player.jailed --;
        game.updatePlayerOnAllClients(this);
    }

    void setBankrupt(boolean b) {
        player.isBankrupt = b;
        game.updatePlayerOnAllClients(this);
    }

    int tryRemoveMoney(int money) {
        int ret = money;

        if(player.balance >= money) {
            player.balance -= money;
        } else if(player.balance + getAssetsSellValue() + getBuildingsSellValue() >= money) {
            game.extendCurrentTurn(20);
            //TODO (ForceAssetManagement) - if they exit the menu or dont send a response in x seconds then autosell
        } else {
            ret = player.balance;
            player.balance = 0;
            player.isBankrupt = true;
        }

        game.updatePlayerOnAllClients(this);
        return ret;
    }

    public void updateBoard() {
        send(PacketType.BOARD_UPDATE, game.getMonoBoard().getBoard());
    }
}
