package ph.adamw.calloji.server.monopoly.card;

import ph.adamw.calloji.server.monopoly.MonoPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class MonoCardPile extends ArrayList<MonoCard> {
    public static final MonoCard[] CHANCE = {
            new MonoCard.MoveMoney("Advance to \"Go\"", 0, 0),
            new MonoCard.GoToJail(),
            new MonoCard.MoveMoney("Advance to Pall Mall. If you pass \"Go\" collection £200", 0, 11),
            new MonoCard.MoveMoney("Take a trip to Marylebone Station and if you pass \"Go\" collect £200",0, 15),
            new MonoCard.MoveMoney("Advance to Trafalgar Square. If you pass \"Go\" collect £200", 0, 24),
            new MonoCard.MoveMoney("Advance to Mayfair", 0, 39),
            new MonoCard("Go back three spaces",false) {
                @Override
                public void handle(MonoPlayer player) {
                    player.moveSpaces(-3);
                }
            },
            new MonoCard.BuildingRepairs("Make general repairs on all of your houses. For each house pay £25. For each hotel pay £100", 25, 100),
            new MonoCard.BuildingRepairs("You are assessed for street repairs: £40 per house, £115 per hotel", 40, 115),
            new MonoCard.MoveMoney("Pay school fees of £150", -150, 0),
            new MonoCard.MoveMoney("\"Drunk in charge\" fine £20", -20, 0),
            new MonoCard.MoveMoney("Speeding fine £15", -15, 0),
            new MonoCard.MoveMoney("Your building loan matures. Receive £150", 150, 0),
            new MonoCard.MoveMoney("You have won a crossword competition. Collect £100", 100, 0),
            new MonoCard.MoveMoney("Bank pays you dividend of £50", 50, 0),
            new MonoCard.GetOutOfJailFree()
    };

    public static final MonoCard[] COMM_CHEST = {
            new MonoCard.MoveMoney("Advance to \"Go\"", 0, 0),
            new MonoCard("Go back to Old Kent Road", false) {
                @Override
                public void handle(MonoPlayer player) {
                    player.moveSpaces(-(player.getPlayer().getBoardPosition() - 1));
                }
            },
            new MonoCard.GoToJail(),
            new MonoCard.MoveMoney("Pay hospital £100", -100, 0),
            new MonoCard.MoveMoney("Doctor's fee. Pay £50", -50, 0),
            new MonoCard.MoveMoney("Pay your insurance premium £50", -50, 0),
            new MonoCard.MoveMoney("Bank error in your favour. Collect £200", 200, 0),
            new MonoCard.MoveMoney("Annuity matures. Collect £100", 100, 0),
            new MonoCard.MoveMoney("You inherit £100", 100, 0),
            new MonoCard.MoveMoney("From sale of stock you get £50", 50, 0),
            new MonoCard.MoveMoney("Receive interest on 7% preference shares: £25", 25, 0),
            new MonoCard.MoveMoney("Income tax refund. Collect £20", 20, 0),
            new MonoCard.MoveMoney("You have won second prize in a beauty contest. Collect £10", 10, 0),
            new MonoCard.GetOutOfJailFree()
    };

    public MonoCardPile(MonoCard... cards) {
        super(Arrays.asList(cards));
        shuffle();
    }

    public void shuffle() {
        for(int i = 0; i < size() - 2; i ++) {
            final int j = ThreadLocalRandom.current().nextInt(i, size());
            add(i, remove(j));
            add(j, remove(i));
        }
    }

    public MonoCard draw() {
        final MonoCard card = get(0);
        remove(card);
        add(card);
        return card;
    }
}
