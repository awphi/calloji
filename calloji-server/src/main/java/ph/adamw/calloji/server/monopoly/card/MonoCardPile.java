package ph.adamw.calloji.server.monopoly.card;

import java.util.ArrayList;
import java.util.List;

public class MonoCardPile {
    private final List<MonoCard> communityChestPile = new ArrayList<>();
    private final List<MonoCard> chancePile = new ArrayList<>();

    public MonoCardPile() {
        // TODO add anonymous/common cards to both piles here
    }

    public MonoCard draw(boolean commChest) {
        final List<MonoCard> pile = commChest ? communityChestPile : chancePile;
        final MonoCard card = pile.get(0);

        pile.remove(card);
        pile.add(card);

        return card;
    }
}
