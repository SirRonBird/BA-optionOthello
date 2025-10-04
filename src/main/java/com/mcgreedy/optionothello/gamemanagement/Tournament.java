package com.mcgreedy.optionothello.gamemanagement;

import java.util.ArrayList;
import java.util.List;

public class Tournament {

    Gamemanager gamemanager;
    int numberOfGames;
    List<Game> games;

    int gamesLeft = 0;

    int blackWins = 0;
    int whiteWins = 0;

    int gamesPlayed = 0;

    public Tournament(Gamemanager gamemanager,int numberOfGames) {
        games = new ArrayList<>();
        this.gamesLeft = this.numberOfGames = numberOfGames;
        this.gamemanager = gamemanager;
    }

    public void addGame(Game game) {
        games.add(game);
        gamesLeft--;
    }

    public void endGame(int winner) {
        gamesPlayed++;
        if (winner == 0) {
            blackWins++;
        } else if (winner == 1) {
            whiteWins++;
        }
    }

    public boolean hasFinished() {
        return gamesLeft == 0;
    }

    public int getWinner() {
        if (blackWins > whiteWins) {
            return 0;
        } else if (whiteWins > blackWins) {
            return 1;
        }
        return -1;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getBlackWins() {
        return blackWins;
    }

    public int getWhiteWins() {
        return whiteWins;
    }

    public List<Game> getGames() {
        return games;
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "blackPlayer=" + gamemanager.blackPlayer +
                ", whitePlayer=" + gamemanager.whitePlayer +
                ", numberOfGames=" + numberOfGames +
                ", games=" + games +
                ", gamesLeft=" + gamesLeft +
                ", blackWins=" + blackWins +
                ", whiteWins=" + whiteWins +
                ", gamesPlayed=" + gamesPlayed +
                '}';
    }
}
