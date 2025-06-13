package com.mcgreedy.optionothello.gamemanagement;

import com.mcgreedy.optionothello.engine.Game;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.ui.MainGUI;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.SaveGameUtils;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
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
  int winner = -1;

  int tournamentWinner = -1;

  boolean isTournament = false;

  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  private static final Logger LOGGER = LogManager.getLogger(Gamemanager.class);

  public Gamemanager() {
    LOGGER.info("Game manager created");
  }

  public void newGame(Player black, Player white) {
    LOGGER.info("Create new game");
    blackPlayer = black;
    whitePlayer = white;

    consecutivePasses = 0;

    LOGGER.debug("Black player: {}", blackPlayer);
    LOGGER.debug("White player: {}", whitePlayer);

    currentGame = new Game();
    if (isTournament) {
      currentTournament.addGame(currentGame);
    }
    isWhiteMove = false;
    currentPlayer = blackPlayer;

    LOGGER.info("Game created");

    //update gui
    if (blackPlayer.getType() == Constants.PLAYER_TYPE.HUMAN
        || whitePlayer.getType() == Constants.PLAYER_TYPE.HUMAN) {
      MainGUI.setPlayerToMove(currentPlayer);
    }

    MainGUI.updatedBoardGrid(currentGame.getBlackBoard(), currentGame.getWhiteBoard(), -1);
    MainGUI.updateGameStandings(currentGame.whitePieces, currentGame.blackPieces);

    if (currentPlayer.getType() == Constants.PLAYER_TYPE.HUMAN) {
      LOGGER.debug("Show possible moves {}",
          currentGame.board.generateAllPossibleMoves(isWhiteMove));
      MainGUI.showPossibleMoves(currentGame.board.generateAllPossibleMoves(isWhiteMove));
    } else {
      Move aiMove = currentPlayer.getMove(currentGame.board);
      if (aiMove.getPosition() == -1) {
        passMove(aiMove);
      } else {
        makeMove(aiMove);
      }
    }
  }

  public void makeMove(Move move) {
    if (currentPlayer.getColor() != move.getColor()) {
      return;
    }

    currentGame.updateBoard(move);
    currentGame.updateScore();

    consecutivePasses = 0;

    MainGUI.updatedBoardGrid(currentGame.getBlackBoard(), currentGame.getWhiteBoard(),
        move.getPosition());
    MainGUI.updateGameStandings(currentGame.whitePieces, currentGame.blackPieces);

    if (currentGame.board.isBoardFull()) {
      endGame();
    } else {
      switchPlayer();
      handleNextMove();
    }
  }

  public void passMove(Move move) {
    if (currentPlayer.getColor() != move.getColor()) {
      return;
    }

    consecutivePasses++;

    currentGame.updateBoard(move);

    if (consecutivePasses >= 2) {
      endGame();
    } else {
      switchPlayer();
      handleNextMove();
    }
  }

  private void handleNextMove() {
    if (currentPlayer.getType() == Constants.PLAYER_TYPE.HUMAN) {
      long allPossibleMoves = currentGame.board.generateAllPossibleMoves(isWhiteMove);
      MainGUI.showPossibleMoves(allPossibleMoves);
    } else {
      executor.schedule(() -> Platform.runLater(() -> {
        Move aiMove = currentPlayer.getMove(currentGame.board);
        if (aiMove.getPosition() == -1) {
          passMove(aiMove);
        } else {
          makeMove(aiMove);
        }
      }), 1, TimeUnit.MILLISECONDS);
    }
  }

  private void switchPlayer() {
    if (isWhiteMove) {
      currentPlayer = blackPlayer;
    } else {
      currentPlayer = whitePlayer;
    }
    isWhiteMove = !isWhiteMove;

    if (blackPlayer.getType() == Constants.PLAYER_TYPE.HUMAN
        || whitePlayer.getType() == Constants.PLAYER_TYPE.HUMAN) {
      MainGUI.setPlayerToMove(currentPlayer);
    }

  }

  private void endGame() {
    LOGGER.info("Game Over!");
    currentGame.updateScore();
    winner = currentGame.whitePieces > currentGame.blackPieces ? 1 : 0;
    if (isTournament) {
      //end current game
      currentTournament.endGame(winner);
      currentGame.setWinner(winner);

      //update GUI
      MainGUI.updateScoreBoard(currentTournament.blackWins, currentTournament.whiteWins,
          currentTournament.gamesPlayed);

      //check if tournament is over
      if (currentTournament.hasFinished()) {
        //get tournament Winner
        tournamentWinner = currentTournament.getWinner();
        //show end of tournament in GUI -> save tournament
        LOGGER.info("Tournament finished with winner {}", tournamentWinner);
        LOGGER.debug("Tournament: {}", currentTournament);
        MainGUI.tournamentOver(tournamentWinner, currentTournament.getGamesPlayed(),
            currentTournament.blackWins, currentTournament.whiteWins);
        isTournament = false;
      } else {
        //start new game
        newGame(currentTournament.blackPlayer, currentTournament.whitePlayer);
      }
    } else {
      currentGame.setWinner(winner);
      MainGUI.gameOver(winner);
    }

  }

  public void saveGame(String name) {
    SaveGameUtils.saveGame(name, winner, blackPlayer, whitePlayer, currentGame);
  }

  public void saveTournament(String name) {
    SaveGameUtils.saveTournament(name, tournamentWinner, blackPlayer, whitePlayer,
        currentTournament);
  }

  public void newTournament(Player black, Player white, int numberOfGames) {
    LOGGER.info("Starting new tournament");

    currentTournament = new Tournament(black, white, numberOfGames);

    isTournament = true;

    newGame(black, white);
  }

  public Game getCurrentGame() {
    return currentGame;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }
}
