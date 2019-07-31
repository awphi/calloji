package ph.adamw.calloji.server.monopoly.card;

import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;
import ph.adamw.calloji.server.monopoly.MonoPlayer;
import ph.adamw.calloji.server.monopoly.MonoStreetPlot;
import ph.adamw.calloji.util.GameConstants;

public abstract class MonoCard {
    @Getter
    private final String text;

    @Getter
    private final boolean isTradable;

    public MonoCard(String text, boolean isTradable) {
        this.text = text;
        this.isTradable = isTradable;
    }

    public abstract void handle(MonoPlayer player);

    public static class MoveMoney extends MonoCard {
        private Integer boardSpot;
        private final int money;

        public MoveMoney(String text, Integer money, int boardSpot) {
            super(text, false);
            this.money = money;
            this.boardSpot = boardSpot;
        }

        @Override
        public void handle(MonoPlayer player) {
            if(boardSpot != null && boardSpot != player.getPlayer().getBoardPosition()) {
                player.moveForward(boardSpot);
            }

            if(money > 0) {
                player.addMoney(money);
            } else if(money < 0) {
                player.tryRemoveMoney(money);
            }
        }
    }

    public static class BuildingRepairs extends MonoCard {
        private final int hotel;
        private final int house;

        public BuildingRepairs(String text, int house, int hotel) {
            super(text, false);
            this.house = house;
            this.hotel = hotel;
        }

        @Override
        public void handle(MonoPlayer player) {
            int sum = 0;

            for(PropertyPlot i : player.getPlayer().getOwnedPlots(player.getGame().getMonoBoard().getBoard())) {
                if(i instanceof StreetPlot) {
                    final MonoStreetPlot sp = (MonoStreetPlot) player.getGame().getMonoBoard().getMonoPlot(i);
                    sum += (sp.getPlot().getHouses() % GameConstants.MAX_HOUSES) * house + (sp.getPlot().getHouses() / GameConstants.MAX_HOUSES) * hotel;
                }
            }

            player.tryRemoveMoney(sum);
        }
    }

    public static class GetOutOfJailFree extends MonoCard {
        public GetOutOfJailFree() {
            super("Get out of jail free.", true);
        }

        @Override
        public void handle(MonoPlayer player) {
            player.setGetOutOfJails(player.getPlayer().getGetOutOfJails() + 1);
        }
    }

    public static class GoToJail extends MonoCard {
        public GoToJail() {
            super("Go to jail. Move directly to jail. Do not pass \"Go\". Do not collect £200", false);
        }

        @Override
        public void handle(MonoPlayer player) {
            player.setJailed(3);
        }
    }
}
