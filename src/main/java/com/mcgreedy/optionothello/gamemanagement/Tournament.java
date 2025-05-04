package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.engine.Game;

import java.util.ArrayList;
import java.util.List;

public class Tournament {

    Player blackPlayer;
    Player whitePlayer;
    int numberOfGames;
    List<Game> games;

    int gamesLeft = 0;

    int blackWins = 0;
    int whiteWins = 0;

    int gamesPlayed = 0;

    public Tournament(Player blackPlayer, Player whitePlayer, int numberOfGames) {
        this.blackPlayer = blackPlayer;
        this.whitePlayer = whitePlayer;

        games = new ArrayList<>();
        this.gamesLeft = this.numberOfGames = numberOfGames;
    }

    public void addGame(Game game) {
        games.add(game);
        gamesLeft--;
    }

    public void endGame(int winner){
        gamesPlayed++;
        if(winner == 0){
            blackWins++;
        } else if(winner == 1){
            whiteWins++;
        }
    }

    public boolean hasFinished(){
        return gamesLeft == 0;
    }

    public int getWinner(){
        if(blackWins > whiteWins){
            return 0;
        } else if(whiteWins > blackWins){
            return 1;
        }
        return -1;
    }

    public int getGamesPlayed(){
        return gamesPlayed;
    }

    /*@Override
    public String toString() {
        return "Tournament{" +
                "blackPlayer=" + blackPlayer +
                ", whitePlayer=" + whitePlayer +
                ", numberOfGames=" + numberOfGames +
                ", games=" + games +
                ", gamesLeft=" + gamesLeft +
                ", blackWins=" + blackWins +
                ", whiteWins=" + whiteWins +
                ", gamesPlayed=" + gamesPlayed +
                '}';
    }*/
}
