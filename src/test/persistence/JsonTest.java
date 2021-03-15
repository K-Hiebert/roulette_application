package persistence;

import model.Roulette;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonTest {
    protected void checkRoulette(Roulette roulette, int startingCash, int endingCash, int roll, boolean hit, List<Integer> bets, List<Integer> amounts) {
        assertEquals(startingCash, roulette.getStartingCash());
        assertEquals(endingCash, roulette.getEndingCash());
        assertEquals(roll, roulette.getRoll());
        assertEquals(hit, roulette.getHit());

        for(Integer bet: bets) {
            assertTrue(roulette.bets.contains(bet));
        }
        assertEquals(bets.size(), roulette.bets.size());
        for(Integer amount: amounts) {
            assertTrue(roulette.betAmounts.contains(amount));
        }
        assertEquals(amounts.size(), roulette.betAmounts.size());
    }
}
