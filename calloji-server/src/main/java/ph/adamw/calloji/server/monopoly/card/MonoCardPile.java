package ph.adamw.calloji.server.monopoly.card;

import ph.adamw.calloji.packet.data.plot.PlotType;

import java.util.ArrayList;
import java.util.Arrays;

public class MonoCardPile extends ArrayList<MonoCard> {
    public static final MonoCard[] CHANCE = {
            new MonoCard.DynamicMove("Advance to go. Collect 200.", PlotType.GO),
            new MonoCard.DynamicMove("Go to jail. Move directly to jail. Do not pass 'Go'. Do not collect 200.", PlotType.JAIL),
            new MonoCard.MoveMoney("Advance to Pall Mall. If you pass go, collect 200.", 0, 11),
            new MonoCard.MoveMoney("Bank pays you dividend of 50.", 50, -1),
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
