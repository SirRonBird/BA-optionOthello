package com.mcgreedy.optionothello.gamemanagement;
import com.mcgreedy.optionothello.engine.Game;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Gamemanager {

    Game currentGame;
    Tournament currentTournament;

    Player blackPlayer;
    Player whitePlayer;

    boolean isWhiteMove = false;


    private static final Logger LOGGER = LogManager.getLogger(Gamemanager.class);

    public Gamemanager() {
        LOGGER.info("Game manager created");
    }

    public void newGame(Player black, Player white) {
        LOGGER.info("Create new game");

        blackPlayer = black;
        whitePlayer = white;


        LOGGER.info("Black player: {}", blackPlayer.toString());
        LOGGER.info("White player: {}", whitePlayer.toString());

        currentGame = new Game();

    }

    public void newTournament(){}
}
