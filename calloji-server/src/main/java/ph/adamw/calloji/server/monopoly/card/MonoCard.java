package ph.adamw.calloji.server.monopoly.card;

import lombok.Getter;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.server.monopoly.MonoPlayer;

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
                player.moveTo(boardSpot);
            }

            if(money > 0) {
                player.addMoney(money);
            } else if(money < 0) {
                player.tryRemoveMoney(money);
            }
        }
    }

    public static class DynamicMove extends MonoCard {
        private final PlotType type;

        public DynamicMove(String text, PlotType type) {
            super(text, false);
            this.type = type;
        }

        @Override
        public void handle(MonoPlayer player) {
            player.moveTo(player.getGame().getMonoBoard().indexOfFirstPlot(type));
        }
    }

    public static class GetOutOfJailFree extends MonoCard {
        public GetOutOfJailFree() {
            super("Get out of jail free.", true);
        }

        @Override
        public void handle(MonoPlayer player) {
            player.getPlayer().getOutOfJails ++;
        }
    }
}
