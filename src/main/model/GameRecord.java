package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// This class saves the recorded roulette games
public class GameRecord implements Writable {

    public List<Roulette> results;
    private int finalCash;


    // MODIFIES: this
    // EFFECTS: initialize the results Lise
    public GameRecord() {
        results = new ArrayList<>();
    }

    // REQUIRES: roulette != null
    // MODIFIES: this
    // EFFECTS: adds the roulette game to results
    public void addRouletteGame(Roulette roulette) {
        results.add(roulette);
    }

    // REQUIRES: 1 <= index <= results.length()
    // MODIFIES: this
    // EFFECTS: removes game at index-1 position from results
    public void deleteRouletteGame(int index) {
        results.remove(index - 1);
    }

    // EFFECTS: returns number of roulette games saved in results
    public int getSize() {
        return results.size();
    }

    public void setFinalCash(int finalCash) {
        this.finalCash = finalCash;
    }

    public int getFinalCash() {
        return finalCash;
    }

    public List<Roulette> getResults() {
        return results;
    }

    // REQUIRES: results.get(gameNum) != null. In other words, the Roulette
    // game for the corresponding gameNum exists
    // EFFECTS: returns a detailed String that describes the roulette game
    public String resultOfGame(int gameNum) {
        Roulette roulette = results.get(gameNum);
        String result = "\n---------------------------------------";
        result += "\nThis is recorded game number " + (gameNum + 1) + "\n";
        result += "You started with $" + roulette.getStartingCash() + "\n";

        int index = 0;
        for (Integer bet: roulette.bets) {
            result += "You bet $" +  roulette.betAmounts.get(index) + " on " + bet + "\n";
            index++;
        }
        result += "You rolled " + roulette.getRoll() + "\n";
        result += "You ended with $" + roulette.getEndingCash() + "\n";
        result += "---------------------------------------\n";
        return result;
    }

    // EFFECTS: returns a String that summarizes all of the roulette games recorded
    public String totalResult() {
        String finalResult = "\nYou recorded a total of " + results.size() + " games that originally started at $1000."
                + " Here are all the recorded games:\n";
        for (int i = 0; i < results.size(); i++) {
            finalResult += resultOfGame(i);
        }
        return finalResult;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("games", resultsToJson());
        json.put("finalCash", finalCash);
        return json;
    }

    // EFFECTS: returns all Roulette games as Json array
    private JSONArray resultsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Roulette result : results) {
            jsonArray.put(result.toJson());
        }
        return jsonArray;
    }

}
