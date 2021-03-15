package model;

import exceptions.BetAmountExceedsCashException;
import exceptions.IllegalSpinException;
import exceptions.NoStartingCashException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class RouletteTest {
    Roulette roulette1;
    Roulette roulette2;
    List<Integer> bets1;
    List<Integer> bets2;
    List<Integer> betsAmount1;
    List<Integer> betsAmount2;
    Integer[] values1, values2, amounts1, amounts2;


    @BeforeEach
    void runBefore() throws NoStartingCashException, BetAmountExceedsCashException {
        values1 = new Integer[] {2, 0, 30};
        amounts1 = new Integer[] {100, 50, 70};
        values2 = new Integer[] {0, 1, 2, 3, 10, 13, 15, 16, 20, 21, 23, 37, 30, 34, 36};
        amounts2 = new Integer[] {10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 15, 15, 15, 15, 15};

        bets1 = Arrays.asList(values1);
        betsAmount1 = Arrays.asList(amounts1);
        bets2 = Arrays.asList(values2);
        betsAmount2 = Arrays.asList(amounts2);

        // these two roulette are assumed to not throw any exception. We test roulettes that
        // throw exception in separate cases
        roulette1 = new Roulette(1000, bets1, betsAmount1);
        roulette2 = new Roulette(800,  bets2, betsAmount2);
    }

    @Test
    void testContructorNoException() {
        try {
            Roulette r1 = new Roulette(300, bets1, betsAmount1);
        } catch (NoStartingCashException e) {
            fail();
        } catch (BetAmountExceedsCashException e) {
            fail();
        }
    }

    @Test
    void testNoStartingCashException() {
        try {
            Roulette r1 = new Roulette(0, bets1, betsAmount1);
            fail();
        } catch (NoStartingCashException e) {
            // pass
        } catch (BetAmountExceedsCashException e) {
            fail();
        }
    }

    @Test
    void testBetAmountExceedsCashException() {
        try {
            Roulette r1 = new Roulette(200, bets1, betsAmount1);
            fail();
        } catch (NoStartingCashException e) {
            fail();
        } catch (BetAmountExceedsCashException e) {
           // pass
        }
    }



    @Test
    void testConstructorValues() {
        assertEquals(1000, roulette1.getStartingCash());
        assertEquals(780, roulette1.getEndingCash());
        assertEquals(3, roulette1.bets.size());
        assertEquals(3, roulette1.betAmounts.size());

        assertEquals(800, roulette2.getStartingCash());
        assertEquals(625, roulette2.getEndingCash());
        assertEquals(15, roulette2.bets.size());
        assertEquals(15, roulette2.betAmounts.size());

        for (Integer integer: values1){
            assertTrue(roulette1.bets.contains(integer));
        }
        for (Integer integer: betsAmount1){
            assertTrue(roulette1.betAmounts.contains(integer));
        }
        for (Integer integer: values2){
            assertTrue(roulette2.bets.contains(integer));
        }
        for (Integer integer: betsAmount2) {
            assertTrue(roulette2.betAmounts.contains(integer));
        }
        assertFalse(roulette1.getHit());
    }

    // this is helper method, so we can only test if the roll eventually hit 36 or not
    @Test
    void testEvaluateBetsNoArgs() throws IllegalSpinException {
        assertEquals(0, roulette1.getRoll());
        boolean hit36 = false;
        for (int i = 0; i < 1000; i++) {
            roulette1.evaluateBets();
            if (roulette1.getRoll() == 36) { // probabilistically, this should happen with 1000 rolls
                hit36 = true;
            }
        }
        assertTrue(hit36);
    }

    @Test
    void testEvaluateBetsNoException()  {
        try {
            roulette1.evaluateBets(5);
            assertEquals(roulette1.getEndingCash(), 780);
            roulette2.evaluateBets(13);
            assertEquals(roulette2.getEndingCash(), 625 + 10 * 37);
        } catch (IllegalSpinException e) {
           fail();
        }
    }

    @Test
    void testIllegalSpinException()  {
        try {
            roulette1.evaluateBets(-1);
            fail();
        } catch (IllegalSpinException e) {
            // pass
        }
        try {
            roulette1.evaluateBets(37);
            fail();
        } catch (IllegalSpinException e) {
            // pass
        }
    }


    // we can test this random method by checking the random number domain is always correct
    @Test
    void testSpin() {
        boolean hit0 = false, hit36 = false;
        for (int i = 0; i < 1000; i++) {
            int num = roulette1.spin();
            assertTrue(num >= 0);
            assertTrue(num <= 37);
            assertEquals(num, roulette1.getRoll());
            if (num == 0) {
                hit0 = true; // probabilistically, this should happen with 1000 rolls
            }
            if (num == 36) {
                hit36 = true; // probabilistically, this should also happen with 1000 rolls
            }
        }
        assertTrue(hit0);
        assertTrue(hit36);
    }

    @Test
    void testToJson() {
        JSONObject jsonRoulette = roulette1.toJson();
        JSONObject scratchJsonRoulette = new JSONObject();
        scratchJsonRoulette.put("startingCash", roulette1.getStartingCash());
        scratchJsonRoulette.put("bets", roulette1.bets);
        scratchJsonRoulette.put("amounts", roulette1.betAmounts);
        scratchJsonRoulette.put("roll", roulette1.getRoll());
        scratchJsonRoulette.put("hit", roulette1.getHit());
        scratchJsonRoulette.put("endingCash", roulette1.getEndingCash());
        assertTrue(jsonRoulette.similar(scratchJsonRoulette));
    }
}