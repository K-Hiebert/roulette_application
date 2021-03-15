# Casino (Current Game Available is Roulette)

A casino game that can be played by anyone (no age restriction!). Feel free to try your 
luck at **roulette**. Intended for anyone who wants to play a quick and easy game.  

## Features 

- Play a game of roulette
- Bet any amount of money on any number
- Keep a record of your gambling game and see your progress

## Motivation

This project was chosen because I wanted to replicate a real life casino where you get to chose how much money you can 
bet, and depending on how lucky you are, you can either win or lose large sums of money. I want this to be a project
 where the user can make various decisions and have a feeling on engagement with the application.    

## User Stories
- As a user, I want to be able to chose which number (0-36) I will place my bet on, and how much I will bet.
- As a user, I want to be able to select whether or not to continue betting on other numbers or roll the ball.
- As a user, I want to be able to choose whether or not to add my roulette game to my game records.
- As a user, after I finish playing, I want to be able to see all of my past games or select individual games to view.
- As a user, after finishing a session of roulette (any # of games), I have the option to save my progress.
- As a user, when starting the application, I have the option to resume from my previous roulette session (if saved) 

-------------------------
Phase 4 Task 2: Made Roulette class robust. Specifically, eliminated REQUIRES cause for the
constructor and the evaluateBets method. These methods throw exceptions that must be caught
or further thrown. All cases of these methods throwing and not throwing the exception are
tested in the RouletteTest class. 

Phase 4 Task 3: Clearly, there are way to many things going on in the 
CasinoGUI class. There is little to no cohesion, and I would focus
mainly on refactoring this class. I would add a ButtonAction class, 
 LoadPanel class, SavedGamePanel class, and a CurrentGamePanel class
 to diversify the responsibility of CasinoGUI. Additionally, similar to what I did with
 the Frame class, I might create classes of Button, TextArea, TextField that 
 extends the Java Swing library, but modified slightly for this specific project. 


