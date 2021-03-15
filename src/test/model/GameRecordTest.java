package model;

import exceptions.BetAmountExceedsCashException;
import exceptions.IllegalSpinException;
import exceptions.NoStartingCashException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameRecordTest {
    GameRecord gameRecord;
    Roulette roulette1;
    Roulette roulette2;
    List<Integer> bets1;
    List<Integer> bets2;
    List<Integer> betsAmount1;
    List<Integer> betsAmount2;
    Integer[] values1, values2, amounts1, amounts2;


    @BeforeEach
    void runBefore() {
        gameRecord = new GameRecord();

        values1 = new Integer[] {2, 0, 30};
        amounts1 = new Integer[] {100, 50, 70};
        values2 = new Integer[] {15, 18};
        amounts2 = new Integer[] {10, 10};

        bets1 = Arrays.asList(values1);
        betsAmount1 = Arrays.asList(amounts1);
        bets2 = Arrays.asList(values2);
        betsAmount2 = Arrays.asList(amounts2);

        try {
            roulette1 = new Roulette(1000, bets1, betsAmount1);
            roulette2 = new Roulette(780,  bets2, betsAmount2);
        } catch (NoStartingCashException e) {
            e.printStackTrace();
        } catch (BetAmountExceedsCashException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testConstructor() {
        assertEquals(0, gameRecord.results.size());
    }

    @Test
    void testAddRouletteGame() {
        gameRecord.addRouletteGame(roulette1);
        assertEquals(1, gameRecord.results.size());
        assertEquals(3, gameRecord.results.get(0).bets.size());

        gameRecord.addRouletteGame(roulette2);
        assertEquals(2, gameRecord.results.size());
        assertEquals(2, gameRecord.results.get(1).bets.size());
    }

    @Test
    void testDeleteRouletteGame() {
        gameRecord.addRouletteGame(roulette1);
        gameRecord.addRouletteGame(roulette2);
        gameRecord.deleteRouletteGame(1);
        assertEquals(1, gameRecord.getSize());
        String result2 = "\n---------------------------------------\n" +
                "This is recorded game number 1\nYou started with $780\n" +
                "You bet $10 on 15\nYou bet $10 on 18\n" +
                "You rolled 0\nYou ended with $760\n" +
                "---------------------------------------\n";
        assertEquals(result2, gameRecord.resultOfGame(0));

    }

    @Test
    void testGetSize() {
        assertEquals(0, gameRecord.getSize());
        gameRecord.addRouletteGame(roulette1);
        assertEquals(1, gameRecord.getSize());
        gameRecord.addRouletteGame(roulette2);
        assertEquals(2, gameRecord.getSize());

    }

    @Test
    void testResultOfGame() {
        try {
            roulette1.evaluateBets(5);
            roulette2.evaluateBets(13);
        } catch (IllegalSpinException e) {
            e.printStackTrace();
        }
        gameRecord.addRouletteGame(roulette1);
        gameRecord.addRouletteGame(roulette2);
        String result1 = "\n---------------------------------------\n" +
                "This is recorded game number 1\nYou started with $1000\n" +
                "You bet $100 on 2\nYou bet $50 on 0\nYou bet $70 on 30\n" +
                "You rolled 5\nYou ended with $780\n" +
                "---------------------------------------\n";
        String result2 = "\n---------------------------------------\n" +
                "This is recorded game number 2\nYou started with $780\n" +
                "You bet $10 on 15\nYou bet $10 on 18\n" +
                "You rolled 13\nYou ended with $760\n" +
                "---------------------------------------\n";
        assertEquals(result1, gameRecord.resultOfGame(0));
        assertEquals(result2, gameRecord.resultOfGame(1));
    }

    @Test
    void testTotalResult() {
        try {
            roulette1.evaluateBets(5);
            roulette2.evaluateBets(13);
        } catch (IllegalSpinException e) {
            e.printStackTrace();
        }gameRecord.addRouletteGame(roulette1);
        gameRecord.addRouletteGame(roulette2);
        String finalResult = "\nYou recorded a total of 2 games that originally started at $1000. " +
                "Here are all the recorded games:\n\n" +
                "---------------------------------------\n" +
                "This is recorded game number 1\nYou started with $1000\n" +
                "You bet $100 on 2\nYou bet $50 on 0\nYou bet $70 on 30\n" +
                "You rolled 5\nYou ended with $780\n" +
                "---------------------------------------\n\n" +
                "---------------------------------------\n" +
                "This is recorded game number 2\nYou started with $780\n" +
        "You bet $10 on 15\nYou bet $10 on 18\n" +
                "You rolled 13\nYou ended with $760\n" +
                "---------------------------------------\n";
        assertEquals(finalResult, gameRecord.totalResult());
    }

    @Test
    void testToJsonAndResultToJson() {
        gameRecord.addRouletteGame(roulette1);
        gameRecord.addRouletteGame(roulette2);
        JSONObject jsonGameRecord = gameRecord.toJson();

        JSONObject scratchJsonGameRecord = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (Roulette result : gameRecord.results) {
            jsonArray.put(result.toJson());
        }
        scratchJsonGameRecord.put("games", jsonArray);
        scratchJsonGameRecord.put("finalCash", gameRecord.getFinalCash());

        assertTrue(jsonGameRecord.similar(scratchJsonGameRecord));
    }

}
