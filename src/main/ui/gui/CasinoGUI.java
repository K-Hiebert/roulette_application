package ui.gui;

import exceptions.BetAmountExceedsCashException;
import exceptions.IllegalSpinException;
import exceptions.NoStartingCashException;
import model.GameRecord;
import model.Roulette;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// Represents the GUI for the casino system.
public class CasinoGUI  implements ActionListener {
    private static final int FRAME_WIDTH = 1400;
    private static final int FRAME_HEIGHT = 1400;
    public static final Color GAME_BACKGROUND = new Color(241, 230, 212);
    public static final Color RECORD_BACKGROUND = new Color(231, 216, 212);
    private static final String JSON_STORE = "././data/gamerecord.json";

    private int numSavedGame;
    private int cash;
    private List<Integer> bets;
    private List<Integer> betAmounts;

    private JsonWriter jsonWriter;
    private JsonReader jsonReader;
    private Roulette currentGame;
    private GameRecord gameRecord;

    Frame frame;
    ScrollFrame gameRecordFrame;

    JPanel loadPanel;
    JPanel savedGamePanel;
    JPanel currentGamePanel;

    JLabel imageLabel;
    JLabel bidLabel;
    JLabel bidAmountLabel;
    JLabel savedGameTopLabel;

    JButton loadGameButton;
    JButton newGameButton;
    JButton displaySavedGamesButton;
    JButton submitButton;
    JButton spinButton;
    JButton addGameYes;
    JButton addGameNo;
    JButton continueGameYes;
    JButton continueGameNo;
    JButton saveSessionYes;
    JButton saveSessionNo;
    JButton deleteButton;

    JTextArea introLabel;
    JTextArea askToAddGameLabel;
    JTextArea continueGameLabel;
    JTextArea resultLabel;
    JTextArea noteLabel;
    JTextArea savedGame;

    JTextField amountField;
    JTextField bidField;
    JTextField deleteField;

    ImageIcon image;

    // MODIFIES: this
    // EFFECTS: starts GUI and asks user whether to load or start new
    public CasinoGUI() {
        init();
        frame = new Frame("Casino Roulette", 1400, 1400);
        askLoad();
    }

    // MODIFIES: this
    // EFFECTS: initializes non-GUI fields
    private void init() {
        numSavedGame = 0;
        jsonWriter = new JsonWriter(JSON_STORE);
        jsonReader = new JsonReader(JSON_STORE);
        gameRecord = new GameRecord();
        this.cash = 1000;
        bets = new ArrayList<>();
        betAmounts = new ArrayList<>();
    }

    // MODIFIES: this
    // EFFECTS: ask user whether or not to load or start new game and displays buttons
    private void askLoad() {
        loadPanel = new JPanel();
        loadPanel.setBounds(0, 0, FRAME_WIDTH, FRAME_HEIGHT);

        JLabel loadLabel = new JLabel("Load from previous session or start new session from $1000?");

        loadGameButton = new JButton("load game");
        loadGameButton.addActionListener(this);

        newGameButton = new JButton("new game");
        newGameButton.addActionListener(this);

        loadPanel.add(loadLabel);
        loadPanel.add(loadGameButton);
        loadPanel.add(newGameButton);

        frame.add(loadPanel);
        frame.setVisible(true);
    }

    @Override
    // MODIFIES: this
    // EFFECTS: reacts to buttons pushed (separate method called for roulette specific buttons)
    public void actionPerformed(ActionEvent e) {
        rouletteActionPreformed(e);
        if (e.getSource() == newGameButton) {
            loadPanel.setVisible(false);
            initializeSavedGamePanel();
            playGame();
        } else if (e.getSource() == loadGameButton) {
            loadPanel.setVisible(false);
            loadGameRecord();
            initializeSavedGamePanel();
            playGame();
        } else if (e.getSource() == deleteButton) {
            deleteGameFromRecords();
        } else if (e.getSource() == displaySavedGamesButton) {
            recordedGameFrame();
        } else if (e.getSource() == saveSessionYes) {
            saveGameRecord();
            frame.setVisible(false);
            frame.dispose();
        } else if (e.getSource() == saveSessionNo) {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    // MODIFIES: this
    // EFFECTS: reacts to buttons pushed related to roulette game
    public void rouletteActionPreformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            bets.add(Integer.parseInt(bidField.getText()));
            int betAmount = Integer.parseInt(amountField.getText());
            betAmounts.add(betAmount);
        } else if (e.getSource() == spinButton) {
            try {
                spin();
            } catch (MalformedURLException exception) {
                System.out.println("MalformedURLException");
            }
        } else if (e.getSource() == addGameYes) {
            addToGameRecords();
            askToContinue();
        } else if (e.getSource() == addGameNo) {
            askToContinue();
        } else if (e.getSource() == continueGameYes) {
            continueGame();
        } else if (e.getSource() == continueGameNo) {
            notContinueGame();
        }
    }

    // MODIFIES: this
    // EFFECTS: initialize currentGamePanel and ask user for bid
    private void playGame() {
        currentGamePanel = new JPanel();
        currentGamePanel.setBackground(GAME_BACKGROUND);
        currentGamePanel.setBounds(0, 0, FRAME_WIDTH / 2, FRAME_HEIGHT);
        frame.add(currentGamePanel);

        introLabel = new JTextArea("Welcome to playing roulette at the casino! You can play as many games as "
                + "you want as long as you have money left! There are 37 pockets with a chance to win x37 if you hit! "
                + "Good luck!", 3, 45);
        introLabel.setBackground(GAME_BACKGROUND);
        introLabel.setFont(new Font("Courier", Font.ITALIC, 25));
        introLabel.setEditable(false);
        introLabel.setLineWrap(true);
        introLabel.setWrapStyleWord(true);
        currentGamePanel.add(introLabel);

        askForBet();
    }


    // MODIFIES: this
    // EFFECTS: deletes game that user inputted from Roulette record, and re-do savedGamePanel accordingly
    private void deleteGameFromRecords() {
        int gameNum = Integer.parseInt(deleteField.getText());
        gameRecord.deleteRouletteGame(gameNum);
        savedGamePanel.setVisible(false);
        initializeSavedGamePanel();
    }


    // MODIFIES: this
    // EFFECTS: Once finished playing, ask user to save sesison or not
    private void notContinueGame() {
        endGameVisibility();
        introLabel.setText("You have finished playing! Do you want to save this session?\n"
                + "Note that when you click a button, the window will close. "
                + "If you want to play with the right panel, do that before clicking yes or no");
        introLabel.setForeground(Color.RED);
        saveSessionYes = new JButton("yes");
        saveSessionYes.addActionListener(this);
        currentGamePanel.add(saveSessionYes);

        saveSessionNo = new JButton("no");
        saveSessionNo.addActionListener(this);
        currentGamePanel.add(saveSessionNo);
    }

    // MODIFIES: this
    // EFFECTS: add current casino game to gameRecord
    private void addToGameRecords() {
        numSavedGame++;
        gameRecord.addRouletteGame(currentGame);
        gameRecord.setFinalCash(cash);

        savedGameTopLabel.setText("Currently, you have " + numSavedGame + " games saved");
        JTextArea resultLabel = new JTextArea("You have saved game number " + numSavedGame + "\n");
        resultLabel.setForeground(Color.BLUE);
        resultLabel.setBackground(RECORD_BACKGROUND);
        resultLabel.setEditable(false);
        resultLabel.setBounds(30, 600, 700, 50);
        resultLabel.setLineWrap(true);
        resultLabel.setWrapStyleWord(true);
        savedGamePanel.add(resultLabel);
    }

    // MODIFIES: this
    // EFFECTS: adds label to ask user for bets, and call initializeBettingFields()
    private void askForBet() {
        noteLabel = new JTextArea("Note: every time you hit sumbit bet, that bet is added. When you are "
                + "satisfied with all your bets, hit spin. REMAINING CASH: " + cash
                + ".(Note: Cash is updated after each spin)");
        noteLabel.setForeground(Color.BLUE);
        noteLabel.setBackground(GAME_BACKGROUND);
        noteLabel.setEditable(false);
        noteLabel.setLineWrap(true);
        noteLabel.setWrapStyleWord(true);
        noteLabel.setBounds(30, 360, 670, 40);
        currentGamePanel.add(noteLabel);

        initializeBettingFields();
    }

    // MODIFIES: this
    // EFFECTS: initializes all relevant betting fields
    private void initializeBettingFields() {
        bidLabel = new JLabel("Enter bet number (0 - 37):");
        bidLabel.setBounds(30, 400, 180, 40);
        currentGamePanel.add(bidLabel);

        bidField = new JTextField();
        bidField.setPreferredSize(new Dimension(100, 40));
        currentGamePanel.add(bidField);

        bidAmountLabel = new JLabel("Enter bet amount:");
        currentGamePanel.add(bidAmountLabel);

        amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(100, 40));
        currentGamePanel.add(amountField);

        submitButton = new JButton("Submit bet");
        submitButton.addActionListener(this);
        currentGamePanel.add(submitButton);

        spinButton = new JButton("SPIN!");
        spinButton.addActionListener(this);
        currentGamePanel.add(spinButton);
    }

    // MODIFIES: this
    // EFFECTS: initializes the savedGamePanel
    private void initializeSavedGamePanel() {
        savedGamePanel = new JPanel();
        savedGamePanel.setBackground(RECORD_BACKGROUND);
        savedGamePanel.setBounds(FRAME_WIDTH / 2, 0, FRAME_WIDTH / 2, FRAME_HEIGHT);
        frame.add(savedGamePanel);

        savedGameTopLabel = new JLabel("Currently, you have " + gameRecord.results.size() + " games saved");
        savedGameTopLabel.setFont(new Font("SANS_SERIF", Font.PLAIN, 20));
        savedGamePanel.add(savedGameTopLabel);

        displaySavedGamesButton = new JButton();
        displaySavedGamesButton.setBounds(1300, 400, 200, 50);
        displaySavedGamesButton.addActionListener(this);
        displaySavedGamesButton.setText("Display all saved game results");
        savedGamePanel.add(displaySavedGamesButton);

        initializeDeleteButton();

        listSavedGames();
    }

    // MODIFIES: this
    // EFFECTS: adds all saved games to savedGamePanel
    private void listSavedGames() {
        for (int i = 0; i < gameRecord.getSize(); i++) {
            savedGame = new JTextArea("You have saved game number " + (i + 1) + "\n");
            savedGame.setForeground(Color.BLUE);
            savedGame.setBackground(RECORD_BACKGROUND);
            savedGame.setEditable(false);
            savedGame.setBounds(30, 600, 700, 50);
            savedGame.setLineWrap(true);
            savedGame.setWrapStyleWord(true);
            savedGamePanel.add(savedGame);
        }
        numSavedGame = gameRecord.getSize();
    }

    // MODIFIES: this
    // EFFECTS: initializes the relevant fields to delete games
    private void initializeDeleteButton() {
        JLabel deleteLabel = new JLabel("If you would like to delete saved game, enter number and hit delete");
        deleteLabel.setBounds(30, 400, 180, 40);
        savedGamePanel.add(deleteLabel);

        deleteField = new JTextField();
        deleteField.setPreferredSize(new Dimension(100, 40));
        savedGamePanel.add(deleteField);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        savedGamePanel.add(deleteButton);
    }

    // MODIFIES: this
    // EFFECTS: loads saved game record from json file
    private void loadGameRecord() {
        try {
            try {
                gameRecord = jsonReader.read();
            } catch (NoStartingCashException e) {
                e.printStackTrace();
            } catch (BetAmountExceedsCashException e) {
                e.printStackTrace();
            }
            System.out.println("Loaded session from " + JSON_STORE);
            cash = gameRecord.getFinalCash();
            System.out.println(cash);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_STORE);
        }
    }

    // MODIFIES: this
    // EFFECTS: shows image of roulette spin, and adds audio component
    private void spin() throws MalformedURLException {
        // sound taken from https://www.wavsource.com/sfx/sfx.htm
        URL soundbyte = new File("././data/cheering.wav").toURI().toURL();
        java.applet.AudioClip clip = java.applet.Applet.newAudioClip(soundbyte);
        clip.play();
        // image taken from https://i.pinimg.com/originals/20/60/9a/20609a367777057295d79802e90fb30d.jpg
        image = new ImageIcon("././data/roulettte.jpg");
        imageLabel = new JLabel(image);
        currentGamePanel.add(imageLabel);
        postSpin();
    }

    // MODIFIES: this
    // EFFECTS: reset game so that new roulette game is started
    private void continueGame() {
        newGameVisibility();
        amountField.setText("");
        bidField.setText("");
        noteLabel.setText("You have elected to start another game. "
                + "When you are satisfied with all your bets, hit spin. REMAINING CASH: " + cash);
    }

    // EFFECTS: saves the workroom to file
    private void saveGameRecord() {
        try {
            jsonWriter.open();
            jsonWriter.write(gameRecord);
            jsonWriter.close();
            System.out.println("Saved session to " + JSON_STORE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_STORE);
        }
    }

    // EFFECTS: creates new scrollable frame that shows entire saved game summary
    private void recordedGameFrame() {
        JTextArea resultTextArea = new JTextArea(70, 90);
        gameRecordFrame = new ScrollFrame(resultTextArea, "Result Summary");
        resultTextArea.setText(gameRecord.totalResult() + "\n\n\n\n\n");
        resultTextArea.setForeground(Color.BLUE);
        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
    }

    // MODIFIES: this
    // EFFECTS: ends visibility for everything in currentGamePanel
    private void endGameVisibility() {
        newGameVisibility();
        noteLabel.setVisible(false);
        amountField.setVisible(false);
        bidField.setVisible(false);
        bidLabel.setVisible(false);
        bidAmountLabel.setVisible(false);
        spinButton.setVisible(false);
        submitButton.setVisible(false);
    }

    // MODIFIES: this
    // EFFECTS: ends visibility for all features related to previous roulette game
    private void newGameVisibility() {
        resultLabel.setVisible(false);
        imageLabel.setVisible(false);
        askToAddGameLabel.setVisible(false);
        askToAddGameLabel.setVisible(false);
        continueGameLabel.setVisible(false);
        addGameYes.setVisible(false);
        addGameNo.setVisible(false);
        continueGameYes.setVisible(false);
        continueGameNo.setVisible(false);
    }

    // MODIFIES: this
    // EFFECTS: evaluates post spin cash, and adds result label to GUI
    private void postSpin() {
        try {
            currentGame = new Roulette(cash, new ArrayList<>(bets), new ArrayList<>(betAmounts));
            currentGame.evaluateBets();
        } catch (NoStartingCashException e) {
            e.printStackTrace();
        } catch (BetAmountExceedsCashException e) {
            e.printStackTrace();
        } catch (IllegalSpinException e) {
            e.printStackTrace();
        }
        cash = currentGame.getEndingCash();
        resultLabel = new JTextArea("The spin was " + currentGame.getRoll()
                + (currentGame.getHit() ? ". Congrats! you hit!" : ". Unfortunately you missed."
        ) + " You have $" + cash + " of cash left");
        resultLabel.setForeground(Color.BLUE);
        resultLabel.setBackground(GAME_BACKGROUND);
        resultLabel.setEditable(false);
        resultLabel.setBounds(30, 600, 700, 50);
        resultLabel.setLineWrap(true);
        resultLabel.setWrapStyleWord(true);
        currentGamePanel.add(resultLabel);

        resetBets();
        askToAddGame();
    }

    // MODIFIES: this
    // EFFECTS: asks user if want to continue playing another game, and add labels and buttons accordingly
    private void askToContinue() {
        continueGameLabel = new JTextArea("Do you want to continue playing another game?");
        continueGameLabel.setBackground(GAME_BACKGROUND);
        continueGameLabel.setEditable(false);
        continueGameLabel.setLineWrap(true);
        continueGameLabel.setWrapStyleWord(true);
        currentGamePanel.add(continueGameLabel);

        continueGameYes = new JButton("yes");
        continueGameYes.addActionListener(this);
        currentGamePanel.add(continueGameYes);

        continueGameNo = new JButton("no");
        continueGameNo.addActionListener(this);
        currentGamePanel.add(continueGameNo);

        askToAddGameLabel.setVisible(false);
        addGameNo.setVisible(false);
        addGameYes.setVisible(false);
    }

    // MODIFIES: this
    // EFFECTS: resets all bets
    private void resetBets() {
        bets.clear();
        betAmounts.clear();
    }

    // MODIFIES: this
    // EFFECTS: asks user if want to add game to gameRecords, and add labels and buttons accordingly
    private void askToAddGame() {
        askToAddGameLabel = new JTextArea("Do you want to add game to records?");
        askToAddGameLabel.setBackground(GAME_BACKGROUND);
        askToAddGameLabel.setEditable(false);
        askToAddGameLabel.setLineWrap(true);
        askToAddGameLabel.setWrapStyleWord(true);
        currentGamePanel.add(askToAddGameLabel);

        addGameYes = new JButton("yes");
        addGameYes.addActionListener(this);
        currentGamePanel.add(addGameYes);

        addGameNo = new JButton("no");
        addGameNo.addActionListener(this);
        currentGamePanel.add(addGameNo);
    }

}
