package ph.adamw.calloji.server.monopoly.card;

import ph.adamw.calloji.packet.data.plot.PlotType;

import java.util.ArrayList;
import java.util.Arrays;

public class MonoCardPile extends ArrayList<MonoCard> {
    public static final MonoCard[] CHANCE = {
            new MonoCard.DynamicMove("Advance to go. Collect 200.", PlotType.GO),
            new MonoCard.MoveMoney("Advance to Pall Mall. If you pass go, collect 200.", 0, 11),
            new MonoCard.MoveMoney("Advance to The Anglel, Islington. If you pass go, collect 200.", 0, 6),
            new MonoCard.DynamicMove("Advance token to nearest Utility. If unowned, you may buy it from the Bank.", PlotType.UTILITY),
            new MonoCard.DynamicMove("Advance token to the nearest Railroad and pay owner the rental to which he is otherwise entitled. If Railroad is unowned, you may buy it from the Bank.", PlotType.STATION),
            new MonoCard.MoveMoney("Bank pays you dividend of $50.", 50, -1),
            new MonoCard.GetOutOfJailFree()

    };

    public static final MonoCard[] COMM_CHEST = {

    };

    public MonoCardPile(MonoCard... cards) {
        super(Arrays.asList(cards));
    }

    public MonoCard draw() {
        final MonoCard card = get(0);
        remove(card);
        add(card);
        return card;
    }
}
