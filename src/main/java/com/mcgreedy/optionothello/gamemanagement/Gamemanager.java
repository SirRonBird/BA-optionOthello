package com.mcgreedy.optionothello.gamemanagement;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcgreedy.optionothello.dtos.SaveGameDTO;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Game;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.ui.MainGUI;
import com.mcgreedy.optionothello.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Gamemanager {

    Game currentGame;
    Tournament currentTournament;

    Player blackPlayer;
    Player whitePlayer;

    boolean isWhiteMove = false;

    Player currentPlayer;

    int consecutivePasses;

    boolean isTournament;

    private static final Logger LOGGER = LogManager.getLogger(Gamemanager.class);

    public Gamemanager() {
        LOGGER.info("Game manager created");
    }

    public void newGame(Player black, Player white) {
        LOGGER.debug("Create new game");

        blackPlayer = black;
        whitePlayer = white;

        consecutivePasses = 0;

        LOGGER.debug("Black player: {}", blackPlayer.toString());
        LOGGER.debug("White player: {}", whitePlayer.toString());

        currentGame = new Game();
        isTournament = false;

        isWhiteMove = false;
        currentPlayer = blackPlayer;

        LOGGER.info("Game created");

        //update gui
        MainGUI.updatedBoardGrid(currentGame.getBlackBoard(), currentGame.getWhiteBoard());
        MainGUI.setPlayerToMove(currentPlayer);
        MainGUI.updateGameStandings(currentGame.whitePieces, currentGame.blackPieces);


        if(currentPlayer.getType() == Constants.PLAYER_TYPE.HUMAN){
            LOGGER.debug("Show possible moves {}", currentGame.board.generateAllPossibleMoves(isWhiteMove));
            MainGUI.showPossibleMoves(currentGame.board.generateAllPossibleMoves(isWhiteMove));
        } else {
            currentPlayer.makeMove();
        }
    }

    public void makeMove(Move move){
        if (currentPlayer.getColor() != move.getColor()) {
            LOGGER.error("Wrong player: {}", move.toString());
        } else {
            LOGGER.debug("Make move at {} for player {}", move.getPosition(), move.getColor());

            //update game
            currentGame.updateBoard(move);
            currentGame.updateScore();

            //reset passes
            consecutivePasses = 0;

            //update gui
            MainGUI.updatedBoardGrid(currentGame.getBlackBoard(), currentGame.getWhiteBoard());
            MainGUI.updateGameStandings(currentGame.whitePieces, currentGame.blackPieces);

            //check if board is full
            if(currentGame.board.isBoardFull()){
                endGame();
            } else {
                switchPlayer();

                //calc all possible moves for human player to show in GUI
                if(currentPlayer.getType() == Constants.PLAYER_TYPE.HUMAN){
                    long allPossibleMoves = currentGame.board.generateAllPossibleMoves(isWhiteMove);
                    MainGUI.showPossibleMoves(allPossibleMoves);
                }

            }
        }
    }

    public void passMove(Move move){
        //Check Player to pass
        if(currentPlayer.getColor() != move.getColor()){
            LOGGER.error("Wrong player passed: {}", move.toString());
            return;
        }
        //add pass to keep track of consecutive passes
        consecutivePasses++;

        //add pass-move to game
        currentGame.updateBoard(move);

        if(consecutivePasses >= 2){
            //game over
            endGame();
        } else {
            switchPlayer();
        }
    }

    private void endGame() {
        LOGGER.info("Game Over!");
        currentGame.updateScore();
        int winner = currentGame.whitePieces > currentGame.blackPieces ? 1 : 0;
        MainGUI.gameOver(winner);
    }

    private void switchPlayer(){
        if(isWhiteMove){
            currentPlayer = blackPlayer;
        } else {
            currentPlayer = whitePlayer;
        }
        isWhiteMove = !isWhiteMove;
        MainGUI.setPlayerToMove(currentPlayer);

        if (currentPlayer.getType() != Constants.PLAYER_TYPE.HUMAN){
            currentPlayer.makeMove();
        }

    }

    public void saveGame(){
        try{
            String projectDir = System.getProperty("user.dir");
            File directory = new File(projectDir, "savegames");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            File gameFile = new File(directory, "savegame.json");

            ObjectMapper objectMapper = new ObjectMapper();
            SaveGameDTO saveGameDTO = createSaveGameDTO();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(gameFile, saveGameDTO);
            LOGGER.info("Saved game to file");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private SaveGameDTO createSaveGameDTO(){
        SaveGameDTO saveGameDTO = new SaveGameDTO();
        SaveGameDTO.GameDetails gameDetails = new SaveGameDTO.GameDetails();

        gameDetails.setId(UUID.randomUUID());

        SaveGameDTO.PlayerDetails blackPlayerDetails = new SaveGameDTO.PlayerDetails();
        blackPlayerDetails.setColor(blackPlayer.getColor());
        blackPlayerDetails.setType(blackPlayer.getType());

        SaveGameDTO.PlayerDetails whitePlayerDetails = new SaveGameDTO.PlayerDetails();
        whitePlayerDetails.setColor(whitePlayer.getColor());
        whitePlayerDetails.setType(whitePlayer.getType());

        gameDetails.setBlackPlayer(blackPlayerDetails);
        gameDetails.setWhitePlayer(whitePlayerDetails);
        gameDetails.setStartBoardBlack(currentGame.board.startBlack);
        gameDetails.setStartBoardWhite(currentGame.board.startWhite);

        List<SaveGameDTO.MoveDetails> moveDetailsList = new ArrayList<>();
        if (currentGame.moveHistory.size() != currentGame.boardHistory.size()) {
            LOGGER.error("MoveHistory and BoardHistory have different sizes: {},{}", currentGame.moveHistory.size(), currentGame.boardHistory.size());
        } else {
            for (int i = 0; i < currentGame.moveHistory.size(); i++) {
                Move move = currentGame.moveHistory.get(i);
                Board board = currentGame.boardHistory.get(i);
                SaveGameDTO.MoveDetails moveDetails = new SaveGameDTO.MoveDetails();

                moveDetails.setColor(move.getColor());
                moveDetails.setPosition(move.getPosition());
                moveDetails.setSearchDepth(move.getSearchDepth());
                moveDetails.setPlayerType(move.getPlayerType());
                moveDetails.setBlackBoardAfterMove(board.getBlack());
                moveDetails.setWhiteBoardAfterMove(board.getWhite());

                moveDetailsList.add(moveDetails);
            }
            gameDetails.setMoves(moveDetailsList);
        }


        saveGameDTO.setGame(gameDetails);
        return saveGameDTO;
    }

    public void newTournament(){}
}
