package persistence;

import exceptions.BetAmountExceedsCashException;
import exceptions.NoStartingCashException;
import model.GameRecord;
import model.Roulette;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

// Represents a class that can reads gamerecord from a JSON file
public class JsonReader {
    private String source;

    // EFFECTS: creates reader and sets source
    public JsonReader(String source) {
        this.source = source;
    }

    // code for this method slightly modified, but
    // taken mostly from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // EFFECTS: reads workroom from file and returns it;
    // throws IOException if an error occurs reading data from file
    public GameRecord read() throws IOException, NoStartingCashException, BetAmountExceedsCashException {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseWorkRoom(jsonObject);
    }

    // code for this method is taken from https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo
    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }
        return contentBuilder.toString();
    }

    // EFFECTS: parse from jsonObject and return new gameRecord
    private GameRecord parseWorkRoom(JSONObject jsonObject)
            throws NoStartingCashException, BetAmountExceedsCashException {
        int cash = jsonObject.getInt("finalCash");
        GameRecord gameRecord = new GameRecord();
        gameRecord.setFinalCash(cash);
        addRoulettes(gameRecord, jsonObject);
        return gameRecord;
    }

    // MODIFIES: gameRecord
    // EFFECTS: parses roulette games from JSON object and adds them to gamerecord
    private void addRoulettes(GameRecord gameRecord, JSONObject jsonObject)
            throws NoStartingCashException, BetAmountExceedsCashException {
        JSONArray jsonArray = jsonObject.getJSONArray("games");
        for (Object json : jsonArray) {
            JSONObject nextRoulette = (JSONObject) json;
            addRoulette(gameRecord, nextRoulette);
        }
    }

    // MODIFIES: gameRecord
    // EFFECTS: parses roulette game from JSON object and adds it to gamerecord. Throws NoStartingCashException or
    // BetAmountExceedsCashException if the added roulette game is illegal
    private void addRoulette(GameRecord gameRecord, JSONObject jsonObject)
            throws NoStartingCashException, BetAmountExceedsCashException {
        int startingCash = jsonObject.getInt("startingCash");
        int endingCash = jsonObject.getInt("endingCash");
        int roll = jsonObject.getInt("roll");
        boolean hit = jsonObject.getBoolean("hit");
        JSONArray jsonBets =  jsonObject.getJSONArray("bets");
        JSONArray jsonAmounts =  jsonObject.getJSONArray("amounts");
        List bets = new LinkedList();
        List amounts = new LinkedList();
        for (int i = 0; i < jsonBets.length(); i++) {
            bets.add(jsonBets.getInt(i));
            amounts.add(jsonAmounts.getInt(i));
        }
        Roulette roulette = new Roulette(startingCash, bets, amounts);
        roulette.setEndingCash(endingCash);
        roulette.setRoll(roll);
        roulette.setHit(hit);
        gameRecord.addRouletteGame(roulette);

    }
}