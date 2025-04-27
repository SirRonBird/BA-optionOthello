package com.mcgreedy.optionothello.gamemanagement;
import com.mcgreedy.optionothello.engine.Game;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.ui.MainGUI;
import com.mcgreedy.optionothello.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
            currentGame.placePiece(move);
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
        currentGame.addMoveToHistory(move);

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

    public void newTournament(){}
}
