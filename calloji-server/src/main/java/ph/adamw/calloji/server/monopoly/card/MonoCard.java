package ph.adamw.calloji.server.monopoly.card;

import lombok.Getter;
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
        private final int boardSpot;
        private final int money;

        public MoveMoney(String text, int money, int boardSpot) {
            super(text, false);
            this.money = money;
            this.boardSpot = boardSpot;
        }

        @Override
        public void handle(MonoPlayer player) {
            if(boardSpot != player.getPlayer().getBoardPosition()) {
                player.moveTo(boardSpot);
            }

            if(money > 0) {
                player.addMoney(money);
            } else if(money < 0) {
                player.tryRemoveMoney(money);
            }
        }
    }
}
