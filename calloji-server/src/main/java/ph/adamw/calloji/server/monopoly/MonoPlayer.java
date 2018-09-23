package ph.adamw.calloji.server.monopoly;

import lombok.AllArgsConstructor;
import ph.adamw.calloji.data.Player;
import ph.adamw.calloji.data.plot.Plot;
import ph.adamw.calloji.data.plot.PlotType;
import ph.adamw.calloji.data.plot.PropertyPlot;
import ph.adamw.calloji.data.plot.StreetPlot;
import ph.adamw.calloji.packet.client.PC;
import ph.adamw.calloji.packet.client.PCBoardUpdate;
import ph.adamw.calloji.server.connection.ClientConnection;

import java.io.Serializable;

@AllArgsConstructor
public class MonoPlayer extends Player implements Serializable {
    private final ClientConnection connection;
    private final MonoGame game;

    public void send(PC p) {
        connection.send(p);
    }

    public long getConnectionId() {
        return connection.getId();
    }

    public void moveTo(int x) {
        moveSpaces((40 % getBoardPosition()) + x);
    }

    public void moveSpaces(int x) {
        final int boardPosition = getBoardPosition();

        setBoardPosition((boardPosition + x) % 40);

        final Plot plot = game.getBoard().plotAt(boardPosition);

        if(plot instanceof PropertyPlot) {
            final PropertyPlot p = (PropertyPlot) plot;

            if(p.getOwner() == null) {
                // Offer to buy (if they have the money) or auction it
            } else if(p.getOwner() != this && !p.isMortgaged()){
                boolean bankrupted = false;

                if(p instanceof StreetPlot) {
                    final StreetPlot st = (StreetPlot) p;
                    bankrupted = tryRemoveMoney(st.getRent());
                } else {
                    if(p.getType() == PlotType.UTILITY) {
                        final int owned = p.getOwner().getOwnedType(PlotType.UTILITY);
                        bankrupted = tryRemoveMoney(owned * 5 + (owned - 2) * x);
                    } else if(p.getType() == PlotType.STATION) {
                        final int owned = p.getOwner().getOwnedType(PlotType.STATION);
                        bankrupted = tryRemoveMoney(25 * (int) Math.pow(2, owned - 1));
                    }
                }

                if(bankrupted) {
                    setBankrupt(true);
                    // Since we can guarantee we're on the server side we can cast over the player
                    ((MonoPlayer) p.getOwner()).addMoney(getBalance());
                    setBalance(0);
                    game.updateClientUIs();
                }
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
                    moveTo(game.getBoard().indexOfFirstPlot(PlotType.JAIL));
                    break;
            }
        }

        game.updateClientBoards();
    }

    public void decrementJail() {
        setJailed(getJailed() - 1);
        updateClientUI();
    }

    public void addMoney(int money) {
        setBalance(getBalance() + money);
        updateClientUI();
    }

    // Returns true if went bankrupt
    public boolean tryRemoveMoney(int money) {
        if(getBalance() >= money) {
            setBalance(getBalance() - money);

            // Return - all good, paid with their cash pile
            return false;
        }

        if(getBalance() + getAssetsSellValue() + getBuildingsSellValue() >= money) {
            game.extendCurrentTurn(20);
            //TODO (ForceAssetManagement) - if they exit the menu or dont send a response in x seconds then autosell
            return false;
        }

        return true;
    }

    public void updateClientUI() {
        //TODO
    }

    public void updateClientBoard() {
        send(new PCBoardUpdate(game.getBoard()));
    }
}
