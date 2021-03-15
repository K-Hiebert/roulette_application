package persistence;

import exceptions.BetAmountExceedsCashException;
import exceptions.NoStartingCashException;
import model.GameRecord;
import model.Roulette;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonWriterTest extends JsonTest {

    Roulette r1, r2;
    GameRecord gameRecord;

    @BeforeEach
    void runBefore() {
        try {
            r1 = new Roulette(1000, new LinkedList<Integer>(Arrays.asList(12, 2, 35, 14)),
                    new LinkedList<Integer>(Arrays.asList(50, 25, 10, 80)));
            r2 = new Roulette(835, new LinkedList<Integer>(Arrays.asList(0, 5, 10, 20)),
                    new LinkedList<Integer>(Arrays.asList(5, 5, 5, 5)));
        } catch (NoStartingCashException e) {
            e.printStackTrace();
        } catch (BetAmountExceedsCashException e) {
            e.printStackTrace();
        }
        r1.setRoll(13);
        r2.setRoll(2);
        gameRecord = new GameRecord();
    }

    @Test
    void testWriterInvalidFile() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testWriterEmptyWorkroom() throws NoStartingCashException, BetAmountExceedsCashException {
        try {
            JsonWriter writer = new JsonWriter("./data/testWriterNoRouletteGameRecord.json");
            writer.open();
            writer.write(gameRecord);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterNoRouletteGameRecord.json");
            gameRecord = reader.read();
            assertEquals(0, gameRecord.getFinalCash());
            assertEquals(0, gameRecord.getSize());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterGeneralWorkroom() throws NoStartingCashException, BetAmountExceedsCashException {
        try {
            gameRecord.addRouletteGame(r1);
            gameRecord.addRouletteGame(r2);
            gameRecord.setFinalCash(700);
            JsonWriter writer = new JsonWriter("./data/testWriterGeneralGameRecord.json");
            writer.open();
            writer.write(gameRecord);
            writer.close();

            JsonReader reader = new JsonReader("./data/testWriterGeneralGameRecord.json");
            gameRecord = reader.read();
            assertEquals(700, gameRecord.getFinalCash());
            List<Roulette> results = gameRecord.getResults();
            assertEquals(2, results.size());
            checkRoulette(results.get(0), 1000, 835,13, false,
                    new LinkedList<Integer>(Arrays.asList(12, 2, 35, 14)), new LinkedList<Integer>(Arrays.asList(50, 25, 10, 80)));
            checkRoulette(results.get(1), 835, 815,2, false,
                    new LinkedList<Integer>(Arrays.asList(0, 5, 10, 20)), new LinkedList<Integer>(Arrays.asList(5, 5, 5, 5)));
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

}
