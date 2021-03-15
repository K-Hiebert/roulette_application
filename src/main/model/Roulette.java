package model;

import exceptions.BetAmountExceedsCashException;
import exceptions.IllegalSpinException;
import exceptions.NoStartingCashException;
import org.json.JSONObject;
import persistence.Writable;

import java.util.List;
import java.util.Random;

// Represents a roulette game
public class Roulette implements Writable {
    private int startingCash;
    private int endingCash;
    private int roll;
    private boolean hit;

    public List<Integer> bets;
    public List<Integer> betAmounts;


    // MODIFIES: this
    // EFFECTS: If starting cash is 0 or less, throw NoStartingCashException. If the bet amount exceeds
    // the cash, throw BetAmountExceedsCashException. Otherwise, initializes bets, betAmounts, startingCash,
    // and set ending cash to starting cash - total bet amount.
    public Roulette(int startingCash, List<Integer> bets, List<Integer> betAmounts)
            throws NoStartingCashException, BetAmountExceedsCashException {
        if (startingCash <= 0) {
            throw new NoStartingCashException("No cash to begin with!");
        }
        int totalBetAmount = 0;
        for (Integer amount: betAmounts) {
            totalBetAmount += amount;
        }
        if (totalBetAmount > startingCash) {
            throw new BetAmountExceedsCashException("You bet more than you have!");
        }
        this.startingCash = startingCash;
        this.endingCash = startingCash - totalBetAmount;
        this.bets = bets;
        this.betAmounts = betAmounts;
        hit = false;
    }

    // MODIFIES: this
    // EFFECTS: helper method to spin and evaluate bets
    public void evaluateBets() throws IllegalSpinException {
        evaluateBets(spin());
    }

    // MODIFIES: this
    // EFFECTS: if bet lower than 0 or higher that 36, throw IllegalSpinException. Otherwise,
    // if bet was placed on the rolled number, then set hit to true
    // and add 37 times the betAmount to endingCash
    public void evaluateBets(int spin) throws IllegalSpinException {
        if (spin < 0 || spin > 36) {
            throw new IllegalSpinException("This spin does not exist for our roulette!");
        }
        this.roll = spin;
        if (bets.contains(spin)) {
            endingCash += 37 * betAmounts.get(bets.indexOf(spin));
            hit = true;
        }
    }

    // EFFECTS: returns a random number between 0 and 36
    public int spin() {
        Random rand = new Random();
        int spin = rand.nextInt(37);
        this.roll = spin;
        return spin;
    }

    public int getStartingCash() {
        return startingCash;
    }

    public void setEndingCash(int endingCash) {
        this.endingCash = endingCash;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public int getEndingCash() {
        return endingCash;
    }

    public boolean getHit() {
        return hit;
    }

    public int getRoll() {
        return roll;
    }

    @Override
    // EFFECTS: returns Json object of Roulette
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("startingCash", startingCash);
        json.put("bets", bets);
        json.put("amounts", betAmounts);
        json.put("roll", roll);
        json.put("hit", hit);
        json.put("endingCash", endingCash);
        return json;
    }
}
