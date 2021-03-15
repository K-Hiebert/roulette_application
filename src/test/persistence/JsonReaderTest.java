package persistence;

import exceptions.BetAmountExceedsCashException;
import exceptions.NoStartingCashException;
import model.GameRecord;
import model.Roulette;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class JsonReaderTest extends JsonTest{

    @Test
    void testFileDoesNotExist() throws NoStartingCashException, BetAmountExceedsCashException {
        JsonReader reader = new JsonReader("./data/nonExistent.json");
        try {
            GameRecord gameRecord = reader.read();
            fail("IOException expected");
        } catch (IOException e) {
            // pass
        }
    }

    @Test
    void testReaderEmptyWorkRoom() throws NoStartingCashException, BetAmountExceedsCashException {
        JsonReader reader = new JsonReader("./data/testReaderNoRouletteGameRecord.json");
        try {
            GameRecord gameRecord = reader.read();
            assertEquals( 0, gameRecord.getFinalCash());
            assertEquals( 0, gameRecord.getSize());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderGeneralWorkRoom() throws NoStartingCashException, BetAmountExceedsCashException {
        JsonReader reader = new JsonReader("./data/testReaderGeneralGameRecord.json");
        try {
            GameRecord gameRecord = reader.read();
            assertEquals(1085, gameRecord.getFinalCash());
            List<Roulette> results = gameRecord.getResults();
            assertEquals(2, results.size());
            checkRoulette(results.get(0), 1000, 910,33, false,
                    new LinkedList<Integer>(Arrays.asList(3, 4, 9, 6)), new LinkedList<Integer>(Arrays.asList(20, 25, 20, 25)));
            checkRoulette(results.get(1), 910, 1085,1, true,
                    new LinkedList<Integer>(Arrays.asList(0, 1)), new LinkedList<Integer>(Arrays.asList(5, 5)));
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }







}
